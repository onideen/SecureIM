package edu.ucsb.cs290g.secureim.crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;


import android.content.Context;
import android.util.Log;

public class KeyReader {

    private static final String TAG = "PublicKeyVerifier";


    public static boolean verify(String username, PublicKey key, Context ctx) {
    	
    	PublicKey savedKey = readPublicKeyFromFile(username, ctx);
    	    	
    	if (savedKey == null) {
    		Log.wtf(TAG, "Couldn't find key");
    		savePublicKey(key, username, ctx);
    	}
    	else {
    		
    		return savedKey.equals(key);
    		
    	}
    	
    	return true;
    }
    public static boolean keyExists(String fileName) {
    	return keyExists(fileName, null);
    }
    public static boolean keyExists(String fileName, Context ctx) {
    	File file;
    	if (ctx == null) {
    		file = new File (fileName);
    		System.out.println(fileName  + (file.exists() ? " EXISTS" : " DOES NOT EXIST"));
    	} else {
    		file = new File (ctx.getFilesDir(), fileName);
    		Log.i(TAG, fileName + (file.exists() ? " EXISTS" : " DOES NOT EXIST"));
    	}
    	return file.exists();
    }
    
	public static void saveToFile(String fileName, BigInteger mod, BigInteger exp, Context ctx) {
		try {
			
			ObjectOutputStream oos;
			
			if (ctx == null){
				oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			}else {
				FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
				oos = new ObjectOutputStream(fos);
			}
			
			oos.writeObject(mod);
			oos.writeObject(exp);
			oos.close();
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void savePublicKey(PublicKey pk, String user) {
		savePublicKey(pk, user, null);
	}
	public static void savePublicKey(PublicKey pk, String user, Context ctx){
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA/ECB/PKCS1Padding");
			RSAPublicKeySpec pub = fact.getKeySpec(pk, RSAPublicKeySpec.class);
			saveToFile(user + "-public.key", pub.getModulus(), pub.getPublicExponent(), ctx);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
	}
	public static void saveKeyPair(KeyPair keypair, String user) {
		saveKeyPair(keypair, user, null);
	}
	public static void saveKeyPair(KeyPair keys, String user, Context ctx){

		try {
			KeyFactory fact = KeyFactory.getInstance("RSA/ECB/PKCS1Padding");
			RSAPublicKeySpec pub = fact.getKeySpec(keys.getPublic(), RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = fact.getKeySpec(keys.getPrivate(), RSAPrivateKeySpec.class);
			saveToFile(user +"-public.key", pub.getModulus(), pub.getPublicExponent(), ctx);
			saveToFile(user +"-private.key", priv.getModulus(), priv.getPrivateExponent(), ctx);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
    }
	
	public static KeyPair readKeyPairFromFile(String user) {
		PublicKey publickey = readPublicKeyFromFile(user);
		PrivateKey privatekey = readPrivateKeyFromFile(user, null);
		return new KeyPair(publickey, privatekey);
	}
	
	public static PublicKey readPublicKeyFromFile(String user){
		return readPublicKeyFromFile(user, null);
	}
	
	public static PublicKey readPublicKeyFromFile(String user, Context ctx) {
		if (!keyExists(user + "-public.key", ctx)) return null;
		
		try {
			ObjectInputStream ois;
			if (ctx == null)
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(user + "-public.key")));
			else {
				FileInputStream fis = ctx.openFileInput(user + "-public.key");
				ois = new ObjectInputStream(fis);
			}
			
			
			BigInteger mod = (BigInteger) ois.readObject();
			BigInteger exp = (BigInteger) ois.readObject();

			java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(mod, exp);
			KeyFactory keyfac = KeyFactory.getInstance("RSA/ECB/PKCS1Padding");
			PublicKey pubkey = keyfac.generatePublic(spec);
			ois.close();
			
			return pubkey;
		
		} catch (FileNotFoundException e) {
			Log.e(TAG, "PublicKeyRead", e);
		} catch (StreamCorruptedException e) {
			Log.e(TAG, "PublicKeyRead", e);
		} catch (IOException e) {
			Log.e(TAG, "PublicKeyRead", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "PublicKeyRead", e);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "PublicKeyRead", e);
		} catch (InvalidKeySpecException e) {
			Log.e(TAG, "PublicKeyRead", e);
		}
		
		return null;
	}
	
	public static PrivateKey readPrivateKeyFromFile(String user) {
		return readPrivateKeyFromFile(user, null);
	}
	
	public static PrivateKey readPrivateKeyFromFile(String user, Context ctx) {
		
		if (!keyExists(user + "-private.key", ctx)) return null;
		
		try {
			FileInputStream fis = null;
			if (ctx == null)
				fis = new FileInputStream(user + "-private.key");
			else
				fis = ctx.openFileInput(user + "-private.key");
			
			
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			BigInteger mod = (BigInteger) ois.readObject();
			BigInteger exp = (BigInteger) ois.readObject();

			java.security.spec.RSAPrivateKeySpec spec = new java.security.spec.RSAPrivateKeySpec(mod, exp);
			KeyFactory keyfac = KeyFactory.getInstance("RSA/ECB/PKCS1Padding");
			PrivateKey privkey = keyfac.generatePrivate(spec);
			fis.close();
			ois.close();

			return privkey;
		
		} catch (FileNotFoundException e) {
			Log.e(TAG, "PrivateKeyRead", e);
		} catch (StreamCorruptedException e) {
			Log.e(TAG, "PrivateKeyRead", e);
		} catch (IOException e) {
			Log.e(TAG, "PrivateKeyRead", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "PrivateKeyRead", e);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "PrivateKeyRead", e);
		} catch (InvalidKeySpecException e) {
			Log.e(TAG, "PrivateKeyRead", e);
		}
		
		return null;
	}
}
