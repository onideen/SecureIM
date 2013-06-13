package edu.ucsb.cs290g.secureim.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import edu.ucsb.cs290g.secureim.MessageObserver;
import edu.ucsb.cs290g.secureim.R;
import edu.ucsb.cs290g.secureim.models.Message;
import edu.ucsb.cs290g.secureim.models.StatusCode;

public class StartConversationTask extends AsyncTask<Message, Void, Boolean> implements MessageObserver {

    private final String TAG = "StartConversationTask";

    private MessageHandler mh;

    private Context ctx;
    private ProgressDialog pDialog;
    private boolean establishedConnection = false;
    private boolean denied = false;


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
    protected Boolean doInBackground(Message... initMessage) {

        mh.sendMessage(initMessage[0]);
        mh.addObserver(this);


        while (!establishedConnection){
            if (denied) return false;
        }
        return true;

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

    }

    @Override
    public void onNewMessage(Message message) {
        if (message.getMessageCode() == StatusCode.CONNECTION_ESTABLISHED) {
            establishedConnection = true;
            mh.removeObserver(this);
        }
        else if (message.getMessageCode() == StatusCode.USER_NOT_FOUND) {
            denied = true;
            mh.removeObserver(this);
        }
    }
}
