package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.FingerprintDAO;
import beans.CharacteristicsBean;
import beans.UniquenessBean;
import datastructures.Fingerprint;
import util.TorCheck;

/**
 * Servlet implementation class TestServlet
 */
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String js_enabled = request.getParameter("js_enabled");
		if (js_enabled == null) {
			/*
			 * The non-JS version of the page.
			 * Perform just a basic fingerprinting.
			 * None of the characteristics that require javascript.
			 */
			Fingerprint fingerprint = getBasicFingerprint(request);
			serveRequest(request, response, fingerprint);
			return;
		} else {
			/*
			 * The JS enabled version of the page.
			 * Do a full fingerprinting.
			 * Will perform javascript fingerprinting then submit fingerprint via a POST request.
			 */
			request.getRequestDispatcher("/WEB-INF/JsTest.jsp").forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Fingerprint fingerprint = getBasicFingerprint(request);

		/*
		 * Extract the rest of the fingerprint from the POST details.
		 */
		fingerprint.setPlatform(request.getParameter("Platform"));
		fingerprint.setPlatformFlash(request.getParameter("PlatformFlash"));
		fingerprint.setPluginDetails(request.getParameter("PluginDetails"));
		{
			Integer timezone;
			try {
				timezone = Integer.parseInt(request.getParameter("TimeZone"));
			} catch (NumberFormatException ex) {
				timezone = null;
			}
			fingerprint.setTimeZone(timezone);
		}
		fingerprint.setScreenDetails(request.getParameter("ScreenDetails"));
		fingerprint.setScreenDetailsFlash(request.getParameter("ScreenDetailsFlash"));
		fingerprint.setLanguageFlash(request.getParameter("LanguageFlash"));
		fingerprint.setFonts(request.getParameter("Fonts"));
		fingerprint.setCharSizes(request.getParameter("CharSizes"));
		fingerprint.setSuperCookie(request.getParameter("SuperCookie"));
		{
			long ourTime = new Date().getTime();
			long theirTime;
			try {
				theirTime = Long.parseLong(request.getParameter("Time"));
			} catch (NumberFormatException ex) {
				// Difference of 0.
				theirTime = ourTime;
			}

			// Get how many minutes our times differ by.
			long difference = (ourTime - theirTime) / (1000 * 60);
			fingerprint.setClockDifference(difference);
		}
		fingerprint.setDateTime(request.getParameter("DateTime"));
		fingerprint.setMathTan(request.getParameter("MathTan"));
		{
			String adsBlocked = request.getParameter("AdsBlocked");
			if (adsBlocked != null) {
				if (adsBlocked.equals("1")) {
					fingerprint.setAdsBlocked(true);
				} else if (adsBlocked.equals("0")) {
					fingerprint.setAdsBlocked(false);
				}
			}
		}
		fingerprint.setCanvas(request.getParameter("Canvas"));
		fingerprint.setWebGLVendor(request.getParameter("WebGLVendor"));
		fingerprint.setWebGLRenderer(request.getParameter("WebGLRenderer"));

		serveRequest(request, response, fingerprint);
	}

	/**
	 * Finalise a request then forward it to the output page.
	 * 
	 * @param request
	 * @param response
	 * @param fingerprint
	 * @throws ServletException
	 * @throws IOException
	 */
	private void serveRequest(HttpServletRequest request, HttpServletResponse response, Fingerprint fingerprint) throws ServletException, IOException {
		CharacteristicsBean chrsbean = new CharacteristicsBean();
		UniquenessBean uniquenessBean = new UniquenessBean();
		FingerprintDAO.processFingerprint(fingerprint, chrsbean, uniquenessBean);
		request.setAttribute("chrBean", chrsbean);
		request.setAttribute("uniquessBean", uniquenessBean);

		/*
		 * Save SampleSetID in a cookie if we have one now.
		 */
		saveSampleSetID(response, fingerprint.getSampleSetID());

		/*
		 * Forward to the output page.
		 */
		request.getRequestDispatcher("/WEB-INF/Output.jsp").forward(request, response);
	}

	/**
	 * Get the basic fingerprint of a request.
	 * This consists of fingerprint properties that can be taken without JavaScript.
	 * 
	 * @param request
	 * @return
	 * @throws ServletException
	 */
	private Fingerprint getBasicFingerprint(HttpServletRequest request) throws ServletException {
		Fingerprint fingerprint = new Fingerprint();

		fingerprint.setUser_agent(getUserAgentHeaderString(request));
		fingerprint.setAccept_headers(getAcceptHeadersString(request));
		fingerprint.setDoNotTrack(getDoNotTrackHeaderString(request));
		fingerprint.setUsingTor(TorCheck.isUsingTor(getServletContext().getInitParameter("serversPublicIP"), request.getLocalPort(), request.getRemoteAddr(), getServletContext().getInitParameter("TorDNSELServer")) == true);

		fingerprint.setIpAddress(getClientIP(request, fingerprint.isUsingTor()));

		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			fingerprint.setCookiesEnabled(true);
		} else {
			fingerprint.setCookiesEnabled(false);
		}
		fingerprint.setSampleSetID(getSampleSetID(request));

		return fingerprint;
	}

	/**
	 * Get the SampleSetID from a request.
	 * The browser uses this to prevent double counting of fingerprints.
	 * 
	 * @param request
	 * @return
	 */
	private Integer getSampleSetID(HttpServletRequest request) throws ServletException {
		Cookie cookies[] = request.getCookies();

		if (cookies == null) {
			// No SampleIDs. Just return an empty list.
			return null;
		}

		// Find the SampleIDs cookie.
		Integer sampleSetID = null;
		for (int i = 0; i < cookies.length; ++i) {
			if (cookies[i].getName().equals("SampleSetID")) {
				try {
					String cookieParts[] = cookies[i].getValue().split("\\|");
					if (cookieParts.length != 3) {
						throw new ServletException("Invalid SampleSetID cookie.");
					}
					/* Get password. */
					String password = getServletContext().getInitParameter("SampleSetIDEncryptionPassword");

					/* Extract the encrypted data, initialisation vector, and salt from the cookie. */
					Decoder decoder = Base64.getDecoder();
					byte ciphertext[] = decoder.decode(cookieParts[0]);
					byte iv[] = decoder.decode(cookieParts[1]);
					byte salt[] = decoder.decode(cookieParts[2]);
					byte plainbytes[];
					try {
						/* Derive the key, given password and salt. */
						SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
						KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
						SecretKey tmp = factory.generateSecret(spec);
						SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

						/* Decrypt the message, given derived key and initialization vector. */
						Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
						cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
						plainbytes = cipher.doFinal(ciphertext);
					} catch (Exception ex) {
						throw new ServletException(ex);
					}
					sampleSetID = ByteBuffer.wrap(plainbytes).asIntBuffer().get();
					break;
				} catch (NumberFormatException ex) {
					// Ignore. Pretend invalid SampleSetID doesn't exist.
				}
			}
		}
		return sampleSetID;
	}

	/**
	 * Save the encrypted SampleSetID in a cookie in the HTTP response.
	 * 
	 * @param response
	 * @param sampleIDs
	 */
	private void saveSampleSetID(HttpServletResponse response, Integer sampleSetID) throws ServletException {
		if (sampleSetID == null) {
			// This should never happen, but if it somehow did it could cause a null pointer exception.
			return;
		} else {
			/* Get password. */
			String password = getServletContext().getInitParameter("SampleSetIDEncryptionPassword");

			/* Generate salt. */
			SecureRandom rand = new SecureRandom();
			byte salt[] = new byte[8];
			rand.nextBytes(salt);

			byte[] iv;
			byte[] ciphertext;
			try {
				/* Derive the key, given password and salt. */
				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
				SecretKey tmp = factory.generateSecret(spec);
				SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

				/* Encrypt the SampleSetID. */
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, secret);
				AlgorithmParameters params = cipher.getParameters();
				iv = params.getParameterSpec(IvParameterSpec.class).getIV();
				ciphertext = cipher.doFinal(ByteBuffer.allocate(4).putInt(sampleSetID).array());
			} catch (Exception ex) {
				throw new ServletException(ex);
			}

			/* Store the encrypted SampleSetID in a cookie */

			Encoder encoder = Base64.getEncoder();
			String cookieStr = encoder.encodeToString(ciphertext) + "|" + encoder.encodeToString(iv) + "|" + encoder.encodeToString(salt);
			Cookie sampleSetIdCookie = new Cookie("SampleSetID", cookieStr);
			sampleSetIdCookie.setMaxAge(60 * 60 * 24 * 30);// 30 days
			response.addCookie(sampleSetIdCookie);
		}
	}

	/**
	 * Get the User-Agent string of a request.
	 * 
	 * @param request
	 * @return
	 */
	private String getUserAgentHeaderString(HttpServletRequest request) {
		String useragent;
		try {
			// We get the header in this more long-winded way so that it may have unicode characters in it, such as Chinese.
			useragent = new String(request.getHeader("User-Agent").getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			// Fallback to regular method.
			useragent = request.getHeader("User-Agent");
		}
		return useragent;
	}

	/**
	 * Get the accept headers of a request.
	 * 
	 * @param request
	 * @return
	 */
	private String getAcceptHeadersString(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		if (accept == null) {
			accept = "";
		}

		String accept_encoding = request.getHeader("accept-encoding");
		if (accept_encoding == null) {
			accept_encoding = "";
		}

		String accept_language = request.getHeader("accept-language");
		if (accept_language == null) {
			accept_language = "";
		}

		try {
			// We get the headers this more long-winded way so that they may have unicode characters inside them.
			return new String(accept.getBytes("ISO8859-1"), "UTF-8") + " " + new String(accept_encoding.getBytes("ISO8859-1"), "UTF-8") + " " + new String(accept_language.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Fallback to regular method.
			return accept + " " + accept_encoding + " " + accept_language;
		}
	}

	/**
	 * Get the DNT header string of a request.
	 * 
	 * @param request
	 * @return
	 */
	private String getDoNotTrackHeaderString(HttpServletRequest request) {
		String dnt;
		try {
			// We get the header in this more long-winded way so that it may have unicode characters in it, such as Chinese.
			dnt = new String(request.getHeader("DNT").getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			// Fallback to regular method.
			dnt = request.getHeader("DNT");
		}
		return dnt;
	}

	/**
	 * Get the client's IP address in the format we want to save it.
	 * Format corresponds to IpAddressHandling context parameter in web.xml.
	 * FULL means save the full IP address.
	 * PARTIAL means zero out the last octet.
	 * Default is PARTIAL.
	 * 
	 * @param request
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String getClientIP(HttpServletRequest request, boolean isUsingTor) throws ServletException {
		if (isUsingTor) {
			String saveTorUserIP = getServletContext().getInitParameter("SaveTorUserIP");
			if (saveTorUserIP != null && saveTorUserIP.equals("1")) {
				// Return full exit-node IP address.
				return request.getRemoteAddr();
			}
		}
		String ipHandling = getServletContext().getInitParameter("IpAddressHandling");
		if (ipHandling != null) {
			if (ipHandling.equals("HASH")) {
				// Collect the salted hash of the IP address.
				try {
					MessageDigest digest = MessageDigest.getInstance("SHA-1");
					digest.reset();
					digest.update(request.getRemoteAddr().getBytes("UTF-8"));
					String salt = getServletContext().getInitParameter("IpHashSalt");
					if (salt != null) {
						digest.update(salt.getBytes("UTF-8"));
					}

					return new String(digest.digest());
				} catch (NoSuchAlgorithmException ex) {
					throw new ServletException(ex);
				} catch (UnsupportedEncodingException ex) {
					throw new ServletException(ex);
				}
			} else if (ipHandling.equals("PARTIAL")) {
				// Collect IP address with last octet set to zero
				String ip = request.getRemoteAddr();
				ip = ip.replaceAll("\\.\\d+$", ".0");
				return ip;
			}
		}
		// Default handling method: Collect full IP address.
		return request.getRemoteAddr();
	}
}
