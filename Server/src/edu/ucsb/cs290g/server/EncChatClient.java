import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import edu.ucsb.cs290g.secureim.models.Message;
import edu.ucsb.cs290g.secureim.models.RSACrypto;

public class EncChatClient {
	ObjectOutputStream oout = null;
	ObjectInputStream oin = null;
	
	public void sendAndRecieve(Message msg){
		Message response = null;
		String content;
		try {
			oout.writeObject(msg);
			response = (Message) oin.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		content = RSACrypto.byteToStringConverter(response.getMessage());
		System.out.println(content);
	}
	
	
	public EncChatClient(int port) {
		Socket clientSocket = null;
		Message response;
		Message send;
		String user = "arne";
		String contact = "other";
		Scanner userIn = new Scanner(System.in);
		
		try {
			clientSocket = new Socket("localhost", 12346);
			clientSocket.setSoTimeout(100);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Please suply a username");
		user = userIn.nextLine();
		
		try {
			oout = new ObjectOutputStream(clientSocket.getOutputStream());
			oin = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			//Autentification Challenge
			response = (Message) oin.readObject();
			String msg = RSACrypto.byteToStringConverter(response.getMessage());
			System.out.println(msg);
			
			//Sending username
			send = new Message(user.getBytes(), contact.getBytes(), msg.getBytes(), msg.getBytes(),230);
			sendAndRecieve(send);

			System.out.println("Who do you want to contact");
			contact = userIn.nextLine();

			send = new Message(user.getBytes(), contact.getBytes(), contact.getBytes(), msg.getBytes(),102);
			sendAndRecieve(send);
			
			while(true){
				
				
				
				if(userIn.hasNext()){
					msg = userIn.nextLine();
					send = new Message(user.getBytes(), contact.getBytes(), msg.getBytes(), msg.getBytes(),103);
					oout.writeObject(send);
				}
				
				try{
					response = (Message) oin.readObject();
				
					msg = RSACrypto.byteToStringConverter(response.getMessage());
					System.out.println(msg);
				}catch (SocketTimeoutException se){
					
				}

			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		System.out.println("ChatClient");
		EncChatClient me = new EncChatClient(12345);

	}
}

