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
import edu.ucsb.cs290g.secureim.models.MessageFactory;
import edu.ucsb.cs290g.secureim.models.StatusCode;
import edu.ucsb.cs290g.secureim.models.User;

public class ConnectionHandlerObjects extends Thread {
	private Socket server;

	private byte[] username;
	private byte[] contact;
	private ObjectOutputStream oout;
	private ObjectInputStream oin;
	private Message response;
	private User me;
	MessageFactory mf;
	
	public ConnectionHandlerObjects(Socket server, KeyPair serverKeyPair) {

		this.server = server;
		this.me = new User("server",serverKeyPair);
	}

	public void sendMessage(Message message){

		try {
			oout.writeObject(mf.forwardFromServer(message));
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

			auth = new Message(me.getUsername().getBytes("UTF-8"), you.getBytes("UTF-8"), message.getBytes("UTF-8"), signature,530);
			auth.setPublicKey(me.getPublickey());
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
		return auth;
	}

	public Message generateServerMessage(String message, int code){


		try {
			//byte[] signature = edu.ucsb.cs290g.secureim.models.RSACrypto.signMessage(message.getBytes("UTF-8"), privkey);
			byte[] signature = message.getBytes();
			Message success = new Message(me.getUsername().getBytes("UTF-8"), username, message.getBytes("UTF-8"), signature, code);
			return success;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
		return null;
	}

	@Override
	public void run() {
		try {

			oout = new ObjectOutputStream(server.getOutputStream());
			PublicKey userKey = null;
			boolean encrypted = false;

			//Sending Authentification Challenge
			oout.writeObject(authenticateMesage());
			System.out.println("Waiting for username");

			InputStream is = server.getInputStream();
			oin = new ObjectInputStream(is);
			response = (Message)oin.readObject();
			username = response.getFrom();

			if(response.getStatusCode() == 230){
				//Encrypted mode
				ListenObject.authenticateUser(username,this);
				System.out.println("New user authenticated as " + RSACrypto.byteToStringConverter(username) + " in encrypted mode");
				
				PublicKey contactKey = KeyReader.readPublicKeyFromFile(RSACrypto.byteToStringConverter(username));
				
				
				if(contactKey==null){
					
					contactKey = response.getPublicKey();
					KeyReader.savePublicKey(contactKey, RSACrypto.byteToStringConverter(username));

					System.out.println("Public Key saved");	
				}
				
				mf = new MessageFactory(me, RSACrypto.byteToStringConverter(contact), contactKey);
				if (contactKey.equals(response.getPublicKey())){
					
					oout.writeObject(mf.createMessage("Authenticate successfully as" + RSACrypto.byteToStringConverter(username)+", who do you want to contact?", StatusCode.AUTH_OK));
				}
				else {
					
					Message m = generateServerMessage("Mismatched keys", StatusCode.UNAUTHORIZED);
					oout.writeObject(m);					
					//Close connection
					server.close();
					return;
				}
				
			
				//USer found etc
				while (userKey == null) {
					response = (Message)oin.readObject();
					contact = response.getDecryptedMessage(me.getPrivateKey()).getBytes();

					System.out.println(RSACrypto.byteToStringConverter(username) + " Trying to reach " +RSACrypto.byteToStringConverter(contact));
					
					boolean userFound = ListenObject.searchUser(contact);
					if(userFound){
						userKey = KeyReader.readPublicKeyFromFile(RSACrypto.byteToStringConverter(contact));
					}else{
						oout.writeObject(mf.createMessage("User not found, please try someone else", StatusCode.USER_NOT_FOUND));
						System.out.printf("User %s was not found\n", RSACrypto.byteToStringConverter(contact));
					}
				}
				
				Message m = mf.createMessage("User found, connecting to " + RSACrypto.byteToStringConverter(contact), StatusCode.CONNECTION_ESTABLISHED);
				m.setPublicKey(userKey);
				oout.writeObject(m);
				System.out.println("User found, connecting...");

				while ((response = (Message) oin.readObject()) != null) {   

					ListenObject.connections.get(RSACrypto.byteToStringConverter(contact)).sendMessage(response);
					
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


