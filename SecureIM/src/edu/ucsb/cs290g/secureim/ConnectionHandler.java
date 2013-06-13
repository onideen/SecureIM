package edu.ucsb.cs290g.secureim;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.ucsb.cs290g.secureim.models.Conversation;
import edu.ucsb.cs290g.secureim.models.Message;
import edu.ucsb.cs290g.secureim.models.RSACrypto;
import edu.ucsb.cs290g.secureim.tasks.ConnectTask;
import edu.ucsb.cs290g.secureim.tasks.MessageHandler;
import edu.ucsb.cs290g.secureim.tasks.PrepareKeyTask;
import edu.ucsb.cs290g.secureim.tasks.StartConversationTask;

/**
 * Created by arnbju on 6/9/13.
 */
public class ConnectionHandler  {

    private static final String TAG = "ConnectionHandler";

    private static ConnectionHandler connectionHandler;
    private List<Conversation> conversations = new ArrayList<Conversation>();


    private boolean isConnected = false;
    private String me;
    private Context ctx;
    private boolean listening = false;

    private PrivateKey myPrivateKey;
    private PublicKey myPublicKey;

    MessageHandler messageHandler;

    private ConnectionHandler(Context ctx) {
        this.ctx = ctx;
    }

    public static ConnectionHandler getConnectionHandler(Context ctx) {

        if (connectionHandler == null ) {
            connectionHandler = new ConnectionHandler(ctx);
        }
        if (ctx != null)
        	connectionHandler.setContext(ctx);
        return connectionHandler;
    }
    

    public void connect(String me) {
        this.isConnected = false;
        this.me = me;

        prepareKeys(me);

        messageHandler = new MessageHandler(ctx);
        messageHandler.start();

        Log.d(TAG, "Trying log in with username " + me);

        new ConnectTask(this.ctx, messageHandler).execute(me);

    }

    private void prepareKeys(String me) {
    	
    	try {
			AsyncTask<String, Void, KeyPair> task = new PrepareKeyTask(ctx).execute(me);
			KeyPair kp = task.get();
			myPrivateKey = kp.getPrivate();
			myPublicKey = kp.getPublic();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	
    }

    public boolean startConversation(String user) {
        if (isConnected){

            Log.d(TAG, "Starting a conversation");

            StartConversationTask ct = new StartConversationTask(this.ctx, messageHandler);

            Message message = new Message(me, "server", user);

            try {
                if (ct.execute(message).get()) {
                    Log.d(TAG, "Starting a conversation with " + user);
                    Conversation conversation = Conversation.startConversation(me, user, conversations);
                    Log.i(TAG, "Conversation between " + me + " and " + user + " started");
                    return true;
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptException", e);
            } catch (ExecutionException e) {
                Log.e(TAG, "ExecutionException", e);
            }


        } else {
            Log.i(TAG, "Not exists, connect " + me);
            connect(me);
        }
        return false;
    }

    public void sendMessage(Message message) {
        if (isConnected && messageHandler != null) {
            messageHandler.sendMessage(message);
        } else {

            connect(me);
        }
    }

    public void listen(MessageObserver observer) {
        if (!listening) {
            listening = true;

        }
        messageHandler.addObserver(observer);
    }


    public void setContext(Context context) {
        this.ctx = context;
    }

    public void connectedAs(String user) {
    	
    	isConnected = true;
        Bundle args = new Bundle();
        args.putString("me", user);


        FragmentManager fm = ((Activity)ctx).getFragmentManager();

        Fragment fragment = new FindUser();

        fragment.setArguments(args);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();

    }
    public PublicKey getPublicKey(){
		return myPublicKey;
    }
}
