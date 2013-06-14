package edu.ucsb.cs290g.secureim.crypto;


import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class RSACrypto {

	public static byte[] getSha256(byte[] text){
		MessageDigest sha;
		try {
			sha = MessageDigest.getInstance("SHA-512");
			text = sha.digest(text);
			return text;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Sha256 not found");
		}
		return null;
	}

	public static byte[] encryptMessage(String text, SecretKey blockkey){
		try {
			Cipher blockcipher = Cipher.getInstance("AES");

			blockcipher.init(Cipher.ENCRYPT_MODE,blockkey);
			byte[] encryptedText = blockcipher.doFinal(text.getBytes());
			return encryptedText;
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
        return null;
	}
/*
	public static byte[] decryptMessage(byte[] text, SecretKey blockkey){
		try {
			Cipher blockcipher = Cipher.getInstance("AES");

			blockcipher.init(Cipher.DECRYPT_MODE,blockkey);
			byte[] decryptedText = blockcipher.doFinal(text);

			return decryptedText;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
        return null;
	}
*/
	public static byte[] signMessage(byte[] text, PrivateKey privkey){
		byte[] hash = getSha256(text);

		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, privkey);
			byte[] signedHash = cipher.doFinal(hash);
			return signedHash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
        return null;

	}

	public static byte[] encryptWithRSA(byte[] intput, PublicKey pubkey){
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);
			byte[] encMsg = cipher.doFinal(intput);
			return encMsg;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}

	public static byte[] decryptWithRSA(byte[] intput, PrivateKey privkey){
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, privkey);
			byte[] encMsg = cipher.doFinal(intput);
			return encMsg;
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
        return null;
	}

	public static SecretKey generateAESkey(int keysize){
		KeyGenerator kgen;
		System.out.println("KEYSIZE: " + keysize);
		try {
			kgen = KeyGenerator.getInstance("AES");
			kgen.init(keysize);
			SecretKey blockkey = kgen.generateKey();
			System.out.println("BLOCK-KEY: " + blockkey.getEncoded().length);
			return blockkey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String byteToStringConverter(byte[] text){
		String response = Arrays.toString(text);
		String[] byteValues = response.substring(1, response.length() - 1).split(",");
		byte[] bytes = new byte[byteValues.length];
		try{
			for (int i=0, len=bytes.length; i<len; i++) {
				bytes[i] = Byte.valueOf(byteValues[i].trim());     
			}
		}catch(NumberFormatException ne){
			return "";
		}
		String str = new String(bytes);

		return str;
	}

	public static KeyPair generateRSAkey(int keysize){
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(keysize);
			KeyPair kp = keyGen.genKeyPair();
			return kp;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean verifySignature(byte[] signedHash, String text, PublicKey pubkey){
		byte[] hash = getSha256(text.getBytes());
		//System.out.println("Has in veri: " + hash);

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, pubkey);
			byte[] computedHash = cipher.doFinal(signedHash);
			System.out.println(Arrays.toString(computedHash));
			return Arrays.equals(hash, computedHash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

        return true;
	}

	public static void main(String[] args) {
		/*
		 * 
		 * TODO: Remove
		
		String message="Hei";
		byte[] test;
		try {
			test = message.getBytes("UTF-8");
			System.out.println("H1: " + Arrays.toString(getSha256(test)));
			System.out.println("H2: " + Arrays.toString(getSha256(test)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		KeyPair key = generateRSAkey(1024);
		byte[] signed = signMessage(message.getBytes(), key.getPrivate());
		System.out.println(Arrays.toString(signed) + " " + signed.length);
		boolean same = verifySignature(signed, message, key.getPublic());
		if(same){
			System.out.println("YES!\n\n" );
		}else{
			System.out.println("no...");
		}
		SecretKey blockkey = generateAESkey(128);
		System.out.println(Arrays.toString(encryptMessage("HEI", blockkey)));
		System.out.println("DeCr: " + byteToStringConverter(decryptMessage(encryptMessage("Hei dette funker jo bra", blockkey), blockkey)));

		Provider[] teste3 = java.security.Security.getProviders();
		System.out.println(Arrays.toString(teste3));
		//saveKeyPair(key, "test", false);

		PrivateKey nypriv = readPrivateKeyFromFile("test-private.key");
		PublicKey nyPub = readPublicKeyFromFile("test-public.key");

		same = verifySignature(signed, message, nyPub);
		if(same){
			System.out.println("YES!\n\n" );
		}else{
			System.out.println("no...");
		}
		*/
	}

}
