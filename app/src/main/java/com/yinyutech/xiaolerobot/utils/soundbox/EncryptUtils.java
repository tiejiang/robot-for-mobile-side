package com.yinyutech.xiaolerobot.utils.soundbox;

import android.annotation.SuppressLint;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class EncryptUtils {
	@SuppressLint("NewApi")
	private static SecretKey generateSecretKey(String key) {
		SecretKey secretKey = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = key.getBytes();
			md.update(bytes, 0, bytes.length);
			byte[] mdBytes = md.digest();
			byte[] truncatedBytes = Arrays.copyOf(mdBytes, 8);
			DESKeySpec keySpec = new DESKeySpec(truncatedBytes);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			secretKey = keyFactory.generateSecret(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return secretKey;
	}

	public static byte[] encryptStr(String str, String key) {
		byte[] bytes = str.getBytes();
		SecretKey secretKey = generateSecretKey(key);
		return encrypt(bytes, secretKey);
	}

	@SuppressLint("TrulyRandom")
	private static byte[] encrypt(byte[] bytes, SecretKey secretKey) {
		byte[] encryptedObj = null;

		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			encryptedObj = cipher.doFinal(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encryptedObj;
	}

	public static byte[] decryptStr(String str, String key) {
		byte[] bytes = str.getBytes();
		SecretKey secretKey = generateSecretKey(key);
		return decrypt(bytes, secretKey);
	}

	public static byte[] decrypt(byte[] bytes, String key) {
		SecretKey secretKey = generateSecretKey(key);
		return decrypt(bytes, secretKey);
	}

	private static byte[] decrypt(byte[] bytes, SecretKey secretKey) {
		byte[] decryptedObj = null;

		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decryptedObj = cipher.doFinal(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decryptedObj;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
