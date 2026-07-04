package com.flame.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class PasswdEncrypt {
	public static String encodeSHA256(String string) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] hash = messageDigest.digest(string.getBytes("UTF-8"));
		return Hex.encodeHexString(hash);
	}

	public static String encryptAES128(String sSrc, String sKey) throws Exception {
		if (sKey == null || sKey.length() != 16) {
			return null;
		}

		byte[] raw = sKey.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));

		return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能。
		//return Hex.encodeHexString(encrypted);
	}

	public static String decryptAES128(String sSrc, String sKey) throws Exception {
		if (sKey == null || sKey.length() != 16) {
			return null;
		}

		byte[] raw = sKey.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);

		byte[] original = cipher.doFinal(new Base64().decode(sSrc));
		return new String(original, "UTF-8");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(encodeSHA256("mqtt"));
	}
}
