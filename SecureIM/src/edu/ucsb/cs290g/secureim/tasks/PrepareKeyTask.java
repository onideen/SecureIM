package edu.ucsb.cs290g.secureim.tasks;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.ucsb.cs290g.secureim.R;
import edu.ucsb.cs290g.secureim.crypto.KeyReader;
import edu.ucsb.cs290g.secureim.crypto.RSACrypto;
import edu.ucsb.cs290g.secureim.models.User;

public class PrepareKeyTask extends AsyncTask<User, Void, KeyPair> {

    private final String TAG = "PrepareKeys";

    private ProgressDialog pDialog;

    public Context ctx;
    
    private User user;
    

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
    protected KeyPair doInBackground(User... users) {
    	user = users[0];

        KeyPair kp = null;
        
        if (KeyReader.keyExists(user.getUsername() + "-private.key", ctx)) {
        	
        	PublicKey pubkey = KeyReader.readPublicKeyFromFile(user.getUsername(), ctx);
            PrivateKey privkey = KeyReader.readPrivateKeyFromFile(user.getUsername(), ctx);

            return new KeyPair(pubkey, privkey);
            
        } else {
        	kp = RSACrypto.generateRSAkey(2048);
        	KeyReader.saveKeyPair(kp, user.getUsername(), ctx);
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
