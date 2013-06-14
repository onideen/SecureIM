package edu.ucsb.cs290g.secureim;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by arnbju on 6/8/13.
 */
public class Login extends Fragment implements View.OnClickListener{

    private final String TAG = "Login";
    //GUI elements
    private EditText usernameField;
    private Button loginButton;

    private String me;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.login, container, false);

        usernameField = (EditText) view.findViewById(R.id.username);
        loginButton = (Button) view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            Log.i(TAG, usernameField.getText().toString());
            me = usernameField.getText().toString().trim();


            InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_HIDDEN);

            ConnectionHandler.getConnectionHandler(getActivity()).connect(me);
        }

    }


}