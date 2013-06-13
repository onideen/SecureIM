package edu.ucsb.cs290g.secureim.models;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import android.content.Context;

import edu.ucsb.cs290g.secureim.ConnectionHandler;
import edu.ucsb.cs290g.secureim.tasks.KeyReader;

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

	public static byte[] signMessage(byte[] text, PrivateKey privkey){
		byte[] hash = getSha256(text);

		try {
			Cipher cipher = Cipher.getInstance("RSA");
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
		}
        return null;

	}

	public static byte[] encryptWithRSA(byte[] intput, PublicKey pubkey){
		try {
			Cipher cipher = Cipher.getInstance("RSA");
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
		}
        return null;
	}

	public static byte[] decryptWithRSA(byte[] intput, PrivateKey privkey){
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privkey);
			byte[] encMsg = cipher.doFinal(intput);
			return encMsg;
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
        return null;
	}

	public static SecretKey generateAESkey(int keysize){
		KeyGenerator kgen;

		try {
			kgen = KeyGenerator.getInstance("AES");
			kgen.init(256);
			SecretKey blockkey = kgen.generateKey();
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
			byte[] publicKey = kp.getPublic().getEncoded();
			return kp;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;


	}

	public static boolean verifySignature(byte[] signedHash, String text, PublicKey pubkey){
		byte[] hash = getSha256(text.getBytes());
		//System.out.println("Has in veri: " + hash);

		try {
			Cipher cipher = Cipher.getInstance("RSA");
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

	/* 
	 *  TODO: Move read and write methods to KeyReader
	 */
	/*
	public static void saveKeyPair(KeyPair keys, String filename, Context ctx){
		KeyFactory fact;

		try {
			fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(keys.getPublic(), RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = fact.getKeySpec(keys.getPrivate(), RSAPrivateKeySpec.class);
			saveToFile(filename +"-public.key", pub.getModulus(), pub.getPublicExponent(), ctx);
			saveToFile(filename +"-private.key", priv.getModulus(), priv.getPrivateExponent(), ctx);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


    }
	
	public static void savePublicKey(PublicKey pk, String user, Context ctx){
		KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(pk, RSAPublicKeySpec.class);
			saveToFile(user + "-public.key", pub.getModulus(), pub.getPublicExponent(), ctx);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
	}

	public static void saveToFile(String fileName,BigInteger mod, BigInteger exp, Context ctx) throws IOException {
		if (ctx != null){
			KeyReader.saveToFile(ctx, fileName, mod, exp);
			
		} else {
			ObjectOutputStream oout = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(fileName)));
			try {
				oout.writeObject(mod);
				oout.writeObject(exp);
			} catch (Exception e) {
				throw new IOException("Unexpected error", e);
			} finally {
				oout.close();
			}
		}
	}

	public static KeyPair readKeyPairFromFile(String user) {
		PublicKey publickey = readPublicKeyFromFile(user + "-public.key");
		PrivateKey privatekey = KeyReader.readPrivateKeyFromFile(user, null);
		return new KeyPair(publickey, privatekey);
	}
	
	public static PublicKey readPublicKeyFromFile(String publicFile){

		try {
			ObjectInputStream Oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(publicFile)));
			BigInteger mod = (BigInteger) Oin.readObject();
			BigInteger exp = (BigInteger) Oin.readObject();

			java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(mod, exp);
			KeyFactory keyfac = KeyFactory.getInstance("RSA");
			PublicKey pubkey = keyfac.generatePublic(spec);
			Oin.close();

			return pubkey;		
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeySpecException e) {}
        return null;
	}
	public static PrivateKey readPrivateKeyFromFile(String privateFile){
		
		try {
			ObjectInputStream Oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(privateFile)));
			BigInteger mod = (BigInteger) Oin.readObject();
			BigInteger exp = (BigInteger) Oin.readObject();

			java.security.spec.RSAPrivateKeySpec spec = new java.security.spec.RSAPrivateKeySpec(mod, exp);
			KeyFactory keyfac = KeyFactory.getInstance("RSA");
			PrivateKey privkey = keyfac.generatePrivate(spec);
			Oin.close();

			return privkey;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
        return null;
	}
*/
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
