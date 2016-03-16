package util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
import javax.servlet.ServletException;

public class Encryption {
	/**
	 * Encrypt an array of integers to a String.
	 * 
	 * @param integers
	 * @param context
	 * @return
	 * @throws ServletException
	 */
	public static String encryptIntegers(int integers[], String password) throws ServletException {
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

			ByteBuffer buff = ByteBuffer.allocate(integers.length * 4);
			for (int i = 0; i < integers.length; ++i) {
				buff.putInt(integers[i]);
			}
			ciphertext = cipher.doFinal(buff.array());
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		/* Store the encrypted SampleSetID in a cookie */

		Encoder encoder = Base64.getEncoder();
		String encryptedStr = encoder.encodeToString(ciphertext) + "|" + encoder.encodeToString(iv) + "|" + encoder.encodeToString(salt);
		return encryptedStr;
	}

	/**
	 * Decrypt an array of integers from a String.
	 * 
	 * @param encrypted
	 * @param context
	 * @return
	 * @throws ServletException
	 */
	public static int[] decryptIntegers(String encrypted, String password) throws ServletException {
		String encryptedParts[] = encrypted.split("\\|");
		if (encryptedParts.length != 3) {
			throw new ServletException("Invalid encrypted string.");
		}

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
		IntBuffer buff = ByteBuffer.wrap(plainbytes).asIntBuffer();
		int integers[] = new int[buff.remaining()];
		for (int i = 0; i < integers.length; ++i) {
			integers[i] = buff.get();
		}
		return integers;
	}
}
