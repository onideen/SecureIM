import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import edu.ucsb.cs290g.secureim.crypto.KeyReader;
import edu.ucsb.cs290g.secureim.crypto.RSACrypto;


public class ListenObject {
	public static String[] users = new String[10];
	public static int currentSpace = 0;
	
	
    public static HashMap<String, ConnectionHandlerObjects> connections = new HashMap<String, ConnectionHandlerObjects>();


	public static void authenticateUser(byte[] username, ConnectionHandlerObjects connectionHandlerObjects){
        connections.put(RSACrypto.byteToStringConverter(username), connectionHandlerObjects);
        System.out.println(RSACrypto.byteToStringConverter(username) + " was added to database");
	}
	
	public static boolean searchUser(byte[] contact){
		System.out.println("Searching database for " + RSACrypto.byteToStringConverter(contact));
        return connections.containsKey(RSACrypto.byteToStringConverter(contact));
	}
	
	public static void portListner(int portnr){
		ServerSocket mainSocket = null;
		Socket clientSocket = null;
		Security.addProvider(new BouncyCastleProvider());
		
		KeyPair kp;
		
		if (KeyReader.keyExists("server-private.key")) {
			kp = KeyReader.readKeyPairFromFile("server");
		}else {
			kp = RSACrypto.generateRSAkey(2048);
			KeyReader.saveKeyPair(kp, "server");
		}
		

		try {
			mainSocket = new ServerSocket(portnr);
		} catch (IOException e) {
			System.out.println("Could not open socket on port " + portnr);
			e.printStackTrace();
		}
 
 		System.out.printf("Server started on port %s\n",portnr);
		while(true){
			try {
				clientSocket = mainSocket.accept();
				System.out.println("A new user connected");
				
				//String test = "testeString";
				//Message msg = new Message(test.getBytes(), test.getBytes(), test.getBytes(), test.getBytes());
				//ObjectOutputStream oout = new ObjectOutputStream(clientSocket.getOutputStream());
				//oout.writeObject(msg);
				ConnectionHandlerObjects t = new ConnectionHandlerObjects(clientSocket, kp);
				t.start();
				
			} catch (IOException e) {
				System.out.println("Accept failed");
				e.printStackTrace();
			}
			
		}
		
 
	}
 
	public static void main(String[] args) {
		int portnr;
		if(args.length == 0){
			System.out.println("No port was specified, listening on default");
			portnr = 12346;
		}else{
			portnr = Integer.parseInt(args[0]);
		}
		portListner(portnr);
		System.out.println("Listening to port  " + portnr);
	}
 
}
