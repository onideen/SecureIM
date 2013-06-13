package edu.ucsb.cs290g.secureim.tasks;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.ucsb.cs290g.secureim.ConnectionHandler;
import edu.ucsb.cs290g.secureim.MessageObserver;
import edu.ucsb.cs290g.secureim.R;
import edu.ucsb.cs290g.secureim.models.RSACrypto;
import edu.ucsb.cs290g.secureim.models.StatusCode;
import edu.ucsb.cs290g.secureim.models.Message;

public class PrepareKeyTask extends AsyncTask<String, Void, KeyPair> {

    private final String TAG = "PrepareKeys";

    private ProgressDialog pDialog;

    public Context ctx;
    
    private String user;
    

    public PrepareKeyTask(Context ctx) {
        this.ctx = ctx;
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
    protected KeyPair doInBackground(String... username) {
    	user = username[0];

        KeyPair kp = null;
        
        if (KeyReader.keyExists(ctx, user + "-private")) {
        	
        	PublicKey pubkey = KeyReader.readPublicKeyFromFile(user, ctx);
            PrivateKey privkey = KeyReader.readPrivateKeyFromFile(user, ctx);

            return new KeyPair(pubkey, privkey);
            
        } else {
        	kp = RSACrypto.generateRSAkey(1024);
        	KeyReader.saveKeyPair(kp, user, ctx);
            
        }
    	
        return kp;
    }


    @Override
    protected void onPostExecute(KeyPair result) {
        super.onPostExecute(result);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        
        

    }

}
