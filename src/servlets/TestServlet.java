package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.tuple.ImmutablePair;

import DAOs.FingerprintDAO;
import beans.PredictionBean;
import beans.CharacteristicsBean;
import beans.UniquenessBean;
import datastructures.ContrastCaptcha;
import datastructures.Fingerprint;
import util.SampleIDs;
import util.TorCheck;
import util.browserPrediction.Predictor;

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
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String show_fingerprint = request.getParameter("show_fingerprint");
		if (show_fingerprint != null) {
			Integer captchaResult = checkCaptcha(request);
			if(captchaResult == null){
				//Captcha was wrong, send it back.
				request.getRequestDispatcher("/captcha").forward(request, response);
				return;
			}
			try {
				show_js_fingerprint(request, response, captchaResult);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			return;
		}

		/*
		 * Get results of questionnaire and save them so they can be pre-filled in later.
		 */
		{
			HttpSession session = request.getSession();
			
			String usingProxy = request.getParameter("usingProxy");
			if(usingProxy == null || usingProxy.equals("")){
				session.setAttribute("usingProxy", null);
			}else if(usingProxy.length() < 20){
				session.setAttribute("usingProxy", usingProxy);
			}
			
			String isSpoofing = request.getParameter("isSpoofing");
			if(isSpoofing == null || isSpoofing.equals("")){
				session.setAttribute("isSpoofing", null);
			}else if(isSpoofing.length() < 20){
				session.setAttribute("isSpoofing", isSpoofing);
			}
			
			String whatOS = request.getParameter("whatOS");
			if(whatOS == null || whatOS.equals("")){
				session.setAttribute("whatOS", null);
			}else if(whatOS.length() < 20){
				session.setAttribute("whatOS", whatOS);
			}
			
			String whatBrowser = request.getParameter("whatBrowser");
			if(whatBrowser == null || whatBrowser.equals("")){
				session.setAttribute("whatBrowser", null);
			}else if(whatBrowser.length() < 20){
				session.setAttribute("whatBrowser", whatBrowser);
			}
		}
		
		
		String js_enabled = request.getParameter("js_enabled");
		if (js_enabled == null) {
			Integer captchaResult = checkCaptcha(request);
			if(captchaResult == null){
				//Captcha was wrong, send it back.
				request.getRequestDispatcher("/captcha").forward(request, response);
				return;
			}
			
			/*
			 * The non-JS version of the page.
			 * Perform just a basic fingerprinting.
			 * None of the characteristics that require javascript.
			 */
			Fingerprint fingerprint = getBasicFingerprint(request);
			fingerprint.setContrastLevel(captchaResult);
			try {
				serveRequest(request, response, fingerprint);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			return;
		} else {
			/*
			 * The JS enabled version of the page.
			 * Do a full fingerprinting.
			 * Will perform javascript fingerprinting then submit fingerprint via a POST request.
			 */
			request.getRequestDispatcher("/WEB-INF/jsTest.jsp").forward(request, response);
			return;
		}
	}

	private Integer checkCaptcha(HttpServletRequest request) throws ServletException  {
		final String CAPTCHA_ERROR_MSG = "Unknown CAPTCHA error. Please try again.";
		final String CAPTCHA_EXPIRED_MSG = "CAPTCHA expired.";
		final String CAPTCHA_INVALID_MSG = "CAPTCHA was incorrect. Please try again.";
		
		HttpSession session = request.getSession(false);
		if(session == null){
			request.setAttribute("error", CAPTCHA_EXPIRED_MSG);
			return null;
		}
		
		ContrastCaptcha captcha;
		try{
			captcha = (ContrastCaptcha) session.getAttribute("captcha");
			if(captcha == null){
				throw new Exception("No CAPTCHA session attribute.");
			}
			session.setAttribute("captcha", null);
		}
		catch(Exception ex){
			request.setAttribute("error", CAPTCHA_ERROR_MSG);
			return null;
		}

		String captchaAnswer = request.getParameter("captchaAnswer");
		if (captchaAnswer == null) {
			//No captcha answer.
			request.setAttribute("error", CAPTCHA_INVALID_MSG);
			return null;
		} else {
			Integer contrastLevel = captcha.isValid(captchaAnswer);
			if(contrastLevel == null){
				request.setAttribute("error", CAPTCHA_INVALID_MSG);
				return null;
			}
			else{
				return contrastLevel; 
			}
		}
	}

	/**
	 * @throws Exception 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	private void show_js_fingerprint(HttpServletRequest request, HttpServletResponse response, Integer captchaResult) throws Exception {
		Fingerprint fingerprint = getBasicFingerprint(request);
		fingerprint.setContrastLevel(captchaResult);

		/*
		 * Extract the rest of the fingerprint from the POST details.
		 */
		try{
			fingerprint.setPlatform(new String(request.getParameter("Platform").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setPlatform(request.getParameter("Platform"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setPlatformFlash(new String(request.getParameter("PlatformFlash").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setPlatformFlash(request.getParameter("PlatformFlash"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setPluginDetails(new String(request.getParameter("PluginDetails").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setPluginDetails(request.getParameter("PluginDetails"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setTimeZone(new String(request.getParameter("TimeZone").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setTimeZone(request.getParameter("TimeZone"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setScreenDetails(new String(request.getParameter("ScreenDetails").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setScreenDetails(request.getParameter("ScreenDetails"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setScreenDetailsFlash(new String(request.getParameter("ScreenDetailsFlash").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setScreenDetailsFlash(request.getParameter("ScreenDetailsFlash"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setLanguageFlash(new String(request.getParameter("LanguageFlash").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setLanguageFlash(request.getParameter("LanguageFlash"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setFonts(new String(request.getParameter("Fonts").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			ex.printStackTrace();
			fingerprint.setFonts(request.getParameter("Fonts"));
		}
		try{
			fingerprint.setFontsJS_CSS(new String(request.getParameter("FontsJS_CSS").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			ex.printStackTrace();
			fingerprint.setFontsJS_CSS(request.getParameter("FontsJS_CSS"));
		}
		try{
			fingerprint.setCharSizes(new String(request.getParameter("CharSizes").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			ex.printStackTrace();
			fingerprint.setCharSizes(request.getParameter("CharSizes"));
		}
		{
			String val = request.getParameter("SuperCookieLocalStorage");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setSuperCookieLocalStorage(true);
				} else if (val.equals("0")) {
					fingerprint.setSuperCookieLocalStorage(false);
				}
			}
		}
		{
			String val = request.getParameter("SuperCookieSessionStorage");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setSuperCookieSessionStorage(true);
				} else if (val.equals("0")) {
					fingerprint.setSuperCookieSessionStorage(false);
				}
			}
		}
		{
			String val = request.getParameter("SuperCookieUserData");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setSuperCookieUserData(true);
				} else if (val.equals("0")) {
					fingerprint.setSuperCookieUserData(false);
				}
			}
		}
		{
			String val = request.getParameter("IndexedDBEnabled");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setIndexedDBEnabled(true);
				} else if (val.equals("0")) {
					fingerprint.setIndexedDBEnabled(false);
				}
			}
		}
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
		try{
			fingerprint.setDateTime(new String(request.getParameter("DateTime").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setDateTime(request.getParameter("DateTime"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setMathTan(new String(request.getParameter("MathTan").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setMathTan(request.getParameter("MathTan"));
			ex.printStackTrace();
		}
		fingerprint.setTbbVersion(request.getParameter("TbbVersion"));
		{
			String val = request.getParameter("AdsBlockedGoogle");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setAdsBlockedGoogle(true);
				} else if (val.equals("0")) {
					fingerprint.setAdsBlockedGoogle(false);
				}
			}
		}
		{
			String val = request.getParameter("AdsBlockedBanner");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setAdsBlockedBanner(true);
				} else if (val.equals("0")) {
					fingerprint.setAdsBlockedBanner(false);
				}
			}
		}
		{
			String val = request.getParameter("AdsBlockedScript");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setAdsBlockedScript(true);
				} else if (val.equals("0")) {
					fingerprint.setAdsBlockedScript(false);
				}
			}
		}
		{
			try {
				fingerprint.setLikeShareFacebook(Integer.parseInt(request.getParameter("LikeShareFacebook")));
			} catch (NumberFormatException ex) {
				fingerprint.setLikeShareFacebook(null);
			}
		}
		{
			try {
				fingerprint.setLikeShareTwitter(Integer.parseInt(request.getParameter("LikeShareTwitter")));
			} catch (NumberFormatException ex) {
				fingerprint.setLikeShareTwitter(null);
			}
		}
		{
			try {
				fingerprint.setLikeShareReddit(Integer.parseInt(request.getParameter("LikeShareReddit")));
			} catch (NumberFormatException ex) {
				fingerprint.setLikeShareReddit(null);
			}
		}
		fingerprint.setCanvas(request.getParameter("Canvas"));
		try{
			fingerprint.setWebGLVendor(new String(request.getParameter("WebGLVendor").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setWebGLVendor(request.getParameter("WebGLVendor"));
			ex.printStackTrace();
		}
		try{
			fingerprint.setWebGLRenderer(new String(request.getParameter("WebGLRenderer").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setWebGLRenderer(request.getParameter("WebGLRenderer"));
			ex.printStackTrace();
		}
		{
			try {
				fingerprint.setTouchPoints(Integer.parseInt(request.getParameter("TouchPoints")));
			} catch (NumberFormatException ex) {
				fingerprint.setTouchPoints(null);
			}
		}
		{
			String val = request.getParameter("TouchEvent");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setTouchEvent(true);
				} else if (val.equals("0")) {
					fingerprint.setTouchEvent(false);
				}
			}
		}
		{
			String val = request.getParameter("TouchStart");
			if (val != null) {
				if (val.equals("1")) {
					fingerprint.setTouchStart(true);
				} else if (val.equals("0")) {
					fingerprint.setTouchStart(false);
				}
			}
		}
		try{
			fingerprint.setAudioFingerprintPXI(new String(request.getParameter("AudioFingerprintPXI").getBytes("ISO8859-1"), "UTF-8"));
		}
		catch(UnsupportedEncodingException ex){
			fingerprint.setAudioFingerprintPXI(request.getParameter("AudioFingerprintPXI"));
		}
		fingerprint.setAudioFingerprintPXIFullBuffer(request.getParameter("AudioFingerprintPXIFullBuffer"));
		fingerprint.setAudioFingerprintNtVc(request.getParameter("AudioFingerprintNtVc"));
		fingerprint.setAudioFingerprintCC(request.getParameter("AudioFingerprintCC"));
		fingerprint.setAudioFingerprintHybrid(request.getParameter("AudioFingerprintHybrid"));
		
		serveRequest(request, response, fingerprint);
	}

	/**
	 * Finalise a request then forward it to the output page.
	 * 
	 * @param request
	 * @param response
	 * @param fingerprint
	 * @throws Exception 
	 */
	private void serveRequest(HttpServletRequest request, HttpServletResponse response, Fingerprint fingerprint) throws Exception {
		CharacteristicsBean chrsBean = new CharacteristicsBean();
		UniquenessBean uniquenessBean = new UniquenessBean();
		PredictionBean predictionBean = Predictor.getPredictionBean(fingerprint);
		ImmutablePair<Integer, String> sampleIds = FingerprintDAO.processFingerprint(fingerprint, request.getSession(false), chrsBean, uniquenessBean);
		if(sampleIds == null){
			response.sendError(500);
			return;
		}
		String sampleUUID = sampleIds.right;
		request.setAttribute("sampleUUID", sampleUUID);
		request.setAttribute("chrsBean", chrsBean);
		request.setAttribute("uniquenessBean", uniquenessBean);
		request.setAttribute("predictionBean", predictionBean);

		/*
		 * Save SampleSetID in a cookie if we have one now.
		 */
		SampleIDs.saveSampleSetID(response, fingerprint.getSampleSetID(), getServletContext());

		/*
		 * Forward to the output page.
		 */
		request.getRequestDispatcher("/WEB-INF/output.jsp").forward(request, response);
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
		fingerprint.setSampleSetID(SampleIDs.getSampleSetID(request, getServletContext()));

		fingerprint.setAllHeaders(getAllHeadersString(request));
		
		{
			HttpSession session = request.getSession(false);
			if(session != null){
				//Get ScreenDetailsCSS
				fingerprint.setScreenDetailsCSS(session.getAttribute("device-width") + "x" + session.getAttribute("device-height"));
				
				//Get fontsCSS
				String fontsStr = null;
				{
					@SuppressWarnings("unchecked")
					TreeSet<String> fonts = (TreeSet<String>)session.getAttribute("fontsNotRequested");
					if(fonts != null){
						fontsStr = "";
						Iterator<String> it = fonts.iterator();
						if(it.hasNext()){
							fontsStr += it.next();
							while(it.hasNext()){
								fontsStr += ", " + it.next();
							}
						}
					}
				}
				fingerprint.setFontsCSS(fontsStr);
				
				fingerprint.setHstsEnabled((Boolean)session.getAttribute("HstsEnabled"));
			}
		}
		
		return fingerprint;
	}

	/**
	 * Get all the HTTP headers from the request so that they can be saved.
	 * @param request
	 * @return
	 */
	private String getAllHeadersString(HttpServletRequest request) {
		String headers = null;
		
		Enumeration<String> en = request.getHeaderNames();
		while(en.hasMoreElements()){
			String headerName = en.nextElement();
			String header = request.getHeader(headerName);
			headers += headerName + ": " + header + "\n";
		}
		
		try{
			headers = new String(headers.getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			headers = request.getHeader("User-Agent");
		}
		return headers;
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
