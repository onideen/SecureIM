package edu.ucsb.cs290g.secureim.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.ucsb.cs290g.secureim.ConnectionHandler;
import edu.ucsb.cs290g.secureim.R;
import edu.ucsb.cs290g.secureim.interfaces.MessageObserver;
import edu.ucsb.cs290g.secureim.models.StatusCode;
import edu.ucsb.cs290g.secureim.models.Message;
import edu.ucsb.cs290g.secureim.models.User;

public class ConnectTask extends AsyncTask<User, Void, Boolean> implements MessageObserver{

    private final String TAG = "ConnectTask";
    private final MessageHandler mh;

    private ProgressDialog pDialog;

    public Context ctx;
    private boolean authenticated = false;
    private boolean denied = false;
    
    private User user;
    

    public ConnectTask(Context ctx, MessageHandler mh) {
        this.ctx = ctx;
        this.mh = mh;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(ctx);
        Log.d(TAG, ctx.getString(R.string.checking_keys));
        pDialog.setMessage(ctx.getString(R.string.checking_keys));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Boolean doInBackground(User... users) {
    	user = users[0];
    	
    	Log.i(TAG, "Sendig public key for: " + user.getUsername());
        Message m = new Message(user.getUsername().getBytes(), "server".getBytes(), user.getUsername().getBytes(), null, StatusCode.ENCRYPT);
        m.setPublicKey(user.getPublickey());
        mh.sendMessage(m);
        Log.i(TAG, "Username sent: " + user.getUsername());
        mh.addObserver(this);

        while (!authenticated){
            if (denied) return false;
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        
        if(result) {
        	ConnectionHandler.getConnectionHandler(ctx).connectedAs(user);
        }

    }

    @Override
    public void onNewMessage(Message message) {
        if (message.getMessageCode() == StatusCode.AUTH_OK) {
            authenticated = true;
            mh.removeObserver(this);
            Log.wtf(TAG, "Trying to get encrypted message");
            Log.wtf(TAG, "The decrypted message is: " + message.getDecryptedMessage(user.getPrivateKey()));
        } else if (message.getMessageCode() == StatusCode.UNAUTHORIZED) {
            denied = true;
            mh.removeObserver(this);
        } else if (message.getMessageCode() == StatusCode.KEY_NOT_FOUND) {
        	Log.i(TAG, "Sending Key");
         }


    }
}
