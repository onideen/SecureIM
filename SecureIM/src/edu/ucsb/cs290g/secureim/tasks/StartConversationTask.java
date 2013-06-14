package edu.ucsb.cs290g.secureim.tasks;

import java.security.PublicKey;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.ucsb.cs290g.secureim.R;
import edu.ucsb.cs290g.secureim.interfaces.MessageObserver;
import edu.ucsb.cs290g.secureim.models.Message;
import edu.ucsb.cs290g.secureim.models.StatusCode;

public class StartConversationTask extends AsyncTask<Message, Void, PublicKey> implements MessageObserver {

    private final String TAG = "StartConversationTask";

    private MessageHandler mh;

    private Context ctx;
    private ProgressDialog pDialog;
    private boolean establishedConnection = false;
    private boolean denied = false;

	private PublicKey contactKey;


    public StartConversationTask(Context ctx, MessageHandler mh) {
        this.ctx = ctx;
        this.mh = mh;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(ctx);
        Log.d(TAG, ctx.getString(R.string.connecting));
        pDialog.setMessage(ctx.getString(R.string.connecting_user));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected PublicKey doInBackground(Message... initMessage) {

        mh.sendMessage(initMessage[0]);
        mh.addObserver(this);


        while (!establishedConnection){
            if (denied) return null;
        }
        return contactKey;

    }

    @Override
    protected void onPostExecute(PublicKey aBoolean) {
        super.onPostExecute(aBoolean);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

    }

    @Override
    public void onNewMessage(Message message) {
        if (message.getStatusCode() == StatusCode.CONNECTION_ESTABLISHED) {
        	contactKey = message.getPublicKey();
        	Log.wtf(TAG, (contactKey == null) ? "ARE YOU KIDDING ME" : "NOT NULL");
            establishedConnection = true;
            mh.removeObserver(this);
        }
        else if (message.getStatusCode() == StatusCode.USER_NOT_FOUND) {
            denied = true;
            mh.removeObserver(this);
        }
    }
}
