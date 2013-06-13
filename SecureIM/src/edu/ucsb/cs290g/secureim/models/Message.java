package edu.ucsb.cs290g.secureim.models;


import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Locale;

import edu.ucsb.cs290g.secureim.crypto.RSACrypto;

/**
 * Created by arnbju on 6/8/13.
 */
public class Message implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8701370852218014995L;
	/**
	 * 
	 */
	private byte[] from;
    private byte[] to;
    private byte[] message;
    private int messageCode;
    private byte[] messageKey;
    private byte[] signature;
    private Timestamp timestamp;
    
    private BigInteger mod;
    private BigInteger exp;

    public Message (String from, String to, String message) {
        this.from = from.trim().toLowerCase(Locale.US).getBytes();
        this.to = to.trim().toLowerCase(Locale.US).getBytes();
        this.message = message.getBytes();
    }

    public Message (String from, String to, PublicKey pubkey){
    	this.from = from.toLowerCase(Locale.US).getBytes();
    	this.to = to.toLowerCase(Locale.US).getBytes();
    	setPublicKey(pubkey);
    }
    
    public Message(byte[] from, byte[] to, byte[] message, byte[] signature, int code) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.signature = signature;
        this.messageCode = code;
        
    }

    public byte[] getFrom() {
        return from;
    }

    public byte[] getTo() {
        return to;
    }

    public byte[] getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public byte[] getSignature() {
        return signature;
    }

    public int getMessageCode(){
        return messageCode;
    }

    public String getSFrom(){
        return byteToStringConverter(from);
    }

    public String getSTo(){
        return byteToStringConverter(to);
    }

    public String getSMessage(){
        return byteToStringConverter(message);
    }

    public static String byteToStringConverter(byte[] text){
        String response = Arrays.toString(text);
        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.valueOf(byteValues[i].trim());
        }

        String str = new String(bytes);

        return str;
    }

    public PublicKey getPublicKey() {
    	java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(mod, exp);
		KeyFactory keyfac;
		try {
			keyfac = KeyFactory.getInstance("RSA");
			PublicKey pubkey = keyfac.generatePublic(spec);
			return pubkey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
    }

    public void setPublicKey(PublicKey publicKey) {
    	KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
			mod = pub.getModulus();
			exp = pub.getPublicExponent();
		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeySpecException e) {
		}
    	
    }
    
    
    @Override
    public String toString() {
    	return RSACrypto.byteToStringConverter(from) + ": " + RSACrypto.byteToStringConverter(to) + " : " + RSACrypto.byteToStringConverter(message) ;
    }

	
}

