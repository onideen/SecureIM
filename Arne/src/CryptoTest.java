import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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


public class CryptoTest {

	public static SecretKey generateAESkey(int keysize){
		KeyGenerator kgen;

		try {
			kgen = KeyGenerator.getInstance("AES");
			kgen.init(keysize);
			SecretKey blockkey = kgen.generateKey();
			return blockkey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
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


	public static byte[] encryptWithRSA(byte[] intput, PublicKey pubkey){
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
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

	private byte[] decryptWithRSA(byte[] intput, PrivateKey privkey){
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privkey);
			byte[] encMsg = cipher.doFinal(intput);
			return encMsg;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such alg");
		} catch (NoSuchPaddingException e) {
			System.out.println("Padding");
		} catch (InvalidKeyException e) {
			System.out.println("Invalid key");
		} catch (IllegalBlockSizeException e) {
			System.out.println("Illegal Blocks");
		} catch (BadPaddingException e) {
			System.out.println("BadPaddingEx");
		}
		return null;
	}
	public CryptoTest() {
		SecretKey supa = generateAESkey(128);
		KeyPair kp = generateRSAkey(2048);
		KeyPair kp2 = generateRSAkey(2048);
		byte[] content = supa.getEncoded();
		byte[] enc = encryptWithRSA(content, kp.getPublic());
		byte[] dec = decryptWithRSA(enc, kp.getPrivate());
		System.out.println(bytesToHex(enc));
		System.out.println(bytesToHex(dec));
		System.out.println(Arrays.equals(dec, content));
	}

	public static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static void main(String[] args) {
		CryptoTest crypt = new CryptoTest();
		
	}



}
