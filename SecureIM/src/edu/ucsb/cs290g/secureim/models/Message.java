package edu.ucsb.cs290g.secureim.models;


import java.io.Serializable;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by arnbju on 6/8/13.
 */
public class Message implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] from;
    private byte[] to;
    private byte[] message;
    private int messageCode;
    private byte[] messageKey;
    private byte[] signature;
    private Timestamp timestamp;
    private PublicKey publicKey;

    public Message (String from, String to, String message) {
        this.from = from.trim().toLowerCase(Locale.US).getBytes();
        this.to = to.trim().toLowerCase(Locale.US).getBytes();
        this.message = message.getBytes();
    }

    public Message (String from, String to, PublicKey pubkey){
    	this.from = from.toLowerCase(Locale.US).getBytes();
    	this.to = to.toLowerCase(Locale.US).getBytes();
    	publicKey = pubkey;
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
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

	
}

