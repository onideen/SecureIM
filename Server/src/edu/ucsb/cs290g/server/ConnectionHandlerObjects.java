import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import javax.crypto.SecretKey;


import edu.ucsb.cs290g.secureim.crypto.KeyReader;
import edu.ucsb.cs290g.secureim.crypto.RSACrypto;
import edu.ucsb.cs290g.secureim.models.Message;
import edu.ucsb.cs290g.secureim.models.StatusCode;

public class ConnectionHandlerObjects extends Thread {
	private Socket server;

	private KeyPair serverKeyPair;

	private PublicKey contactKey;
	private byte[] username;
	private byte[] contact;
	private ObjectOutputStream oout;
	private ObjectInputStream oin;
	private Message response;
	private String me = "Server";
	
	public ConnectionHandlerObjects(Socket server, KeyPair serverKeyPair) {

		this.server = server;
		this.serverKeyPair = serverKeyPair;
	}


	public void sendMessage(byte[] username, byte[] contact, byte[] message, int code){

		//byte[] signature = edu.ucsb.cs290g.secureim.models.RSACrypto.signMessage(message, privkey);
		byte[] signature = message;
		Message newMessage = new Message(username,contact,message, signature,code);
		try {
			oout.writeObject(newMessage);
		} catch (IOException e) {
			System.out.println("Could not send message in sendMessage");

		}
	}

	public Message authenticateMesage(){

		Message auth = null;
		try {
			String you = "User";
			String message = "Authenticate yourself. Challenge:";

			//byte[] signature = edu.ucsb.cs290g.secureim.models.RSACrypto.signMessage(message.getBytes("UTF-8"), privkey);
			byte[] signature = you.getBytes();

			auth = new Message(me.getBytes("UTF-8"), you.getBytes("UTF-8"), message.getBytes("UTF-8"), signature,530);
			auth.setPublicKey(serverKeyPair.getPublic());
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
		return auth;
	}

	public Message generateServerMessage(String message, int code){


		try {
			//byte[] signature = edu.ucsb.cs290g.secureim.models.RSACrypto.signMessage(message.getBytes("UTF-8"), privkey);
			byte[] signature = message.getBytes();
			Message success = new Message(me.getBytes("UTF-8"), username, message.getBytes("UTF-8"), signature, code);
			return success;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
		return null;
	}

	public Message generateEncryptedServerMessage(String message, int code){


		try {
			byte[] signature = RSACrypto.signMessage(message.getBytes("UTF-8"), serverKeyPair.getPrivate());
			SecretKey symKey = RSACrypto.generateAESkey(128);
			byte[] encKey = RSACrypto.encryptWithRSA(symKey.getEncoded(), contactKey);
			byte[] encMsg = RSACrypto.encryptMessage(message, symKey);
			byte[] encSnd = RSACrypto.encryptWithRSA(me.getBytes(), contactKey);
			byte[] encRcv = RSACrypto.encryptWithRSA(contact,contactKey);
			Message srvMsg = new Message(encSnd, encRcv, encMsg, signature, code);
			return srvMsg;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
		return null;
	}

	@Override
	public void run() {
		try {

			oout = new ObjectOutputStream(server.getOutputStream());
			boolean userFound = false;
			boolean encrypted = false;

			//Sending Authentification Challenge
			oout.writeObject(authenticateMesage());
			System.out.println("Waiting for username");

			InputStream is = server.getInputStream();
			oin = new ObjectInputStream(is);
			response = (Message)oin.readObject();
			username = response.getFrom();

			if(response.getMessageCode() == 230){
				//Encrypted mode
				ListenObject.authenticateUser(username,this);
				System.out.println("New user authenticated as " + RSACrypto.byteToStringConverter(username) + " in encrypted mode");
				
				contactKey = KeyReader.readPublicKeyFromFile(RSACrypto.byteToStringConverter(username));
				
				if(contactKey==null){
					
					contactKey = response.getPublicKey();
					KeyReader.savePublicKey(contactKey, RSACrypto.byteToStringConverter(username));

					System.out.println("Public Key saved");
					/*
					System.out.println("Key not found, please supply");
					oout.writeObject(generateServerMessage("Key not found, please supply",StatusCode.KEY_NOT_FOUND));
					
					response = (Message)oin.readObject();
					contactKey = response.getPublicKey();

					*/
				}
				
				oout.writeObject(generateServerMessage("Authenticate successfully as" + RSACrypto.byteToStringConverter(username)+", who do you want to contact?", StatusCode.AUTH_OK));

				
			}else{
				//Unecrypted mode
				ListenObject.authenticateUser(username,this);
				System.out.println("New user authenticated as " + RSACrypto.byteToStringConverter(username) + " with response code " + response.getMessageCode());

				
				oout.writeObject(generateServerMessage("Authenticate successfully as " + RSACrypto.byteToStringConverter(username)+", who do you want to contact?",202));
				while(!userFound){
					response = (Message)oin.readObject();
					contact = response.getMessage();

					System.out.println(RSACrypto.byteToStringConverter(username) + " Trying to reach " +RSACrypto.byteToStringConverter(contact));
					userFound = ListenObject.searchUser(contact);
					if(!userFound){
						oout.writeObject(generateServerMessage("User not found, please try someone else",404));
						System.out.printf("User %s was not found\n", RSACrypto.byteToStringConverter(contact));
					}
				}
				oout.writeObject(generateServerMessage("User found, connecting to " + RSACrypto.byteToStringConverter(contact),200));
				System.out.println("User found, connecting...");

				while ((response = (Message) oin.readObject()) != null) {   

					ListenObject.connections.get(RSACrypto.byteToStringConverter(contact)).sendMessage(username, contact, response.getMessage(),200);
					
					System.out.printf("%s: %s\n", RSACrypto.byteToStringConverter(username),RSACrypto.byteToStringConverter(response.getMessage()));


				}
			}
		} catch (IOException e) {
			System.out.printf("%s disconnected ungracefully\n", username);
		} catch (ClassNotFoundException e) {
			System.out.println("Could not convert Object");
		}
	}
}


