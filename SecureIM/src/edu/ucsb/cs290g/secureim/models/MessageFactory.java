package edu.ucsb.cs290g.secureim.models;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import android.content.Context;
import edu.ucsb.cs290g.secureim.crypto.KeyReader;
import edu.ucsb.cs290g.secureim.crypto.RSACrypto;

public class MessageFactory {

	
	
	private String username ;
	private String to;
	private PublicKey toKey;
	private PublicKey serverKey;
	private PrivateKey myKey;
	private Context ctx;
	
	
	
	public MessageFactory (User from, String to, PublicKey contactKey, Context ctx) {
		this.username = from.getUsername();
		this.to = to;
		this.toKey = contactKey;
		this.myKey = from.getPrivateKey();
		this.ctx = ctx;
		
		serverKey = KeyReader.readPublicKeyFromFile("server", ctx);
		
		if (contactKey == null) {
			toKey = serverKey;
		}
	}
	
	public MessageFactory (User from, String to, PublicKey contactKey){
		this(from, to, contactKey, null);
	}
	
	public MessageFactory(User from, Context ctx) {
		this (from, "server", null, ctx);
	}


	public Message createMessage(String Message, int statuscode){
		SecretKey sc = RSACrypto.generateAESkey(128);
		byte[] encMsg = RSACrypto.encryptMessage(Message, sc);
		byte[] signature = RSACrypto.signMessage(Message.getBytes(), myKey);
		byte[] encSnd = RSACrypto.encryptWithRSA(username.getBytes(), (ctx != null) ? serverKey : toKey);
		byte[] encRcv = RSACrypto.encryptWithRSA(to.getBytes(), (ctx != null) ? serverKey : toKey);
		byte[] encBlockKey = RSACrypto.encryptWithRSA(sc.getEncoded(), toKey);					

		return new Message(encSnd,encRcv,encMsg,signature,encBlockKey, statuscode);
		
	}

	public Message forwardFromServer(Message message) {
		
		byte[] receiver = message.getDecryptReceiver(myKey).getBytes();
		
		System.out.println(receiver);
		message.setReceiver(RSACrypto.encryptWithRSA(receiver, toKey));
		byte[] sender = message.getDecryptSender(myKey).getBytes();
		message.setSender(RSACrypto.encryptWithRSA(sender, toKey));
		
		return message;
		
	}
	
	
	public Message generateServerMessage(String message, int code){

		try {
			//byte[] signature = edu.ucsb.cs290g.secureim.models.RSACrypto.signMessage(message.getBytes("UTF-8"), privkey);
			byte[] signature = message.getBytes();
			Message success = new Message(username.getBytes(), to.getBytes(), message.getBytes("UTF-8"), signature, code);
			return success;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
		return null;
	}
	
	
}
