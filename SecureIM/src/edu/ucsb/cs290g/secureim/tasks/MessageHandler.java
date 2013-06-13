package edu.ucsb.cs290g.secureim.tasks;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import edu.ucsb.cs290g.secureim.crypto.KeyReader;
import edu.ucsb.cs290g.secureim.interfaces.MessageObserver;
import edu.ucsb.cs290g.secureim.models.Message;

public class MessageHandler extends Thread {

    private final String TAG = "MessageHandler";

    private Socket socket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private static String servername = "192.168.0.20";
    private static int serverport = 12346;

    private boolean listening = true;
    private boolean waitingToSend;

    private List<MessageObserver> observers;

	private Context ctx;

    public MessageHandler(Context ctx) {
    	this.ctx = ctx;
        observers = new ArrayList<MessageObserver>();
    }

    public void addObserver(MessageObserver observer){
        if(!observers.contains(observer))
            observers.add(observer);
    }

    public void removeObserver(MessageObserver observer) {
        if (observers.contains(observer))
            observers.remove(observer);
    }


    @Override
    public void run() {
        try{
            Log.d(TAG, "Connecting");

            InetAddress serverAddr = InetAddress.getByName(servername);
            socket = new Socket(serverAddr, serverport);
            socket.setSoTimeout(1000);

            Message m = null;
            Log.i(TAG, "Waiting for messages");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            m = (Message) in.readObject();
            Log.i(TAG, "Got message" + m );
            
            
            KeyReader.verify("server", m.getPublicKey(), ctx);
            
            
            if (m.getMessageCode() != 530) {
                Log.i(TAG, "Couldn't get 530 message from server");
                return;
            }

            while (socket.isConnected()) {
                
                if (waitingToSend){
                    listening = false;
                } else {
                	Log.i(TAG, "Listening..");
                    listening = true;
                    try {
                        m = (Message)in.readObject();
                        Log.d(TAG, "Message: " + m);
                        new Thread(new FireNewMessage(m)).start();

                    }catch (SocketTimeoutException se){}
                    catch (ClassNotFoundException e) {
                        Log.e(TAG, "ClassNotFound", e);
                    }
                }
            }

            Log.i(TAG, "Out of the loop");
        }catch (IOException e){
            Log.e(TAG, "Lost connection with server", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFound", e);
//		} catch (InterruptedException e1) {
//			Log.e(TAG, "InterrupedException", e1);
//		} catch (ExecutionException e1) {
//			Log.e(TAG, "ExecutionException", e1);		
		}

    }
    

    public void sendMessage(Message message){
        waitingToSend = true;
        Log.i(TAG, "Waiting to send");
        while(listening){}
        try {
            out.writeObject(message);
            Log.i(TAG, "Sending: " + message);
        } catch (IOException e) {
        	Log.e(TAG, "Failed to send", e);
        }
        waitingToSend = false;
        listening = true;

    }
    
    class FireNewMessage implements Runnable {
    	
    	private Message message;
    	
    	public FireNewMessage(Message message) {
    		this.message = message;
    	}
    	
		@Override
		public void run() {
			for (MessageObserver observer : observers) {
	            observer.onNewMessage(message);
	        }
		}
    	
    }
}
