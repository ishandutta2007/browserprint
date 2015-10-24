package util;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleIDs {
	/**
	 * Get the SampleSetID from a request.
	 * The browser uses this to prevent double counting of fingerprints.
	 * 
	 * @param request
	 * @param context
	 * @return
	 * @throws ServletException
	 */
	public static Integer getSampleSetID(HttpServletRequest request, ServletContext context) throws ServletException {
		Cookie cookies[] = request.getCookies();

		if (cookies == null) {
			// No SampleIDs. Just return an empty list.
			return null;
		}

		// Find the SampleIDs cookie.
		Integer sampleSetID = null;
		for (int i = 0; i < cookies.length; ++i) {
			if (cookies[i].getName().equals("SampleSetID")) {
				sampleSetID = decryptInteger(cookies[i].getValue(), context);
				break;
			}
		}
		return sampleSetID;
	}

	/**
	 * Save the encrypted SampleSetID in a cookie in the HTTP response.
	 * 
	 * @param response
	 * @param sampleIDs
	 * @param context
	 */
	public static void saveSampleSetID(HttpServletResponse response, Integer sampleSetID, ServletContext context) throws ServletException {
		if (sampleSetID == null) {
			// This should never happen, but if it somehow did it could cause a null pointer exception.
			return;
		} else {
			String cookieStr = encryptInteger(sampleSetID, context);
			Cookie sampleSetIdCookie = new Cookie("SampleSetID", cookieStr);
			sampleSetIdCookie.setMaxAge(60 * 60 * 24 * 30);// 30 days
			response.addCookie(sampleSetIdCookie);
		}
	}

	public static Integer decryptInteger(String encrypted, ServletContext context) throws ServletException {
		String encryptedParts[] = encrypted.split("\\|");
		if (encryptedParts.length != 3) {
			throw new ServletException("Invalid encrypted string.");
		}
		/* Get password. */
		String password = context.getInitParameter("SampleSetIDEncryptionPassword");

		/* Extract the encrypted data, initialisation vector, and salt from the cookie. */
		Decoder decoder = Base64.getDecoder();
		byte ciphertext[] = decoder.decode(encryptedParts[0]);
		byte iv[] = decoder.decode(encryptedParts[1]);
		byte salt[] = decoder.decode(encryptedParts[2]);
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
		return ByteBuffer.wrap(plainbytes).asIntBuffer().get();
	}

	/**
	 * Encrypt an integer to a String.
	 * 
	 * @param integer
	 * @param context
	 * @return
	 * @throws ServletException
	 */
	public static String encryptInteger(Integer integer, ServletContext context) throws ServletException {
		/* Get password. */
		String password = context.getInitParameter("SampleSetIDEncryptionPassword");

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
			ciphertext = cipher.doFinal(ByteBuffer.allocate(4).putInt(integer).array());
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		/* Store the encrypted SampleSetID in a cookie */

		Encoder encoder = Base64.getEncoder();
		String encryptedStr = encoder.encodeToString(ciphertext) + "|" + encoder.encodeToString(iv) + "|" + encoder.encodeToString(salt);
		return encryptedStr;
	}
}
