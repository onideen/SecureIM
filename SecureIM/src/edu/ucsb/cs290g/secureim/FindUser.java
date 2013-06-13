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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by arnbju on 6/8/13.
 */
public class FindUser extends Fragment implements View.OnClickListener{

    private final String TAG = "FindUser";
    //GUI elements
    private EditText connectNameField;
    private Button connectButton;

    private String user;
    private String me;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        me = getArguments().getString("me");

        Log.i(TAG, "MY NAME IS: " + me);
        View view = inflater.inflate(R.layout.find_user, container, false);

        connectNameField = (EditText) view.findViewById(R.id.connectname);
        connectButton = (Button) view.findViewById(R.id.connect_button);

        connectButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == connectButton) {

            try {
                InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (Exception e) {}

            user = connectNameField.getText().toString().trim().toLowerCase();
            Log.i("test", "Kom hit");
            if (ConnectionHandler.getConnectionHandler(getActivity()).startConversation(user)) {
                Log.i("test", "..og hit");
                FragmentManager fm = getFragmentManager();

                Bundle args = new Bundle();

                args.putString("me", me);
                args.putString("user", user);

                Fragment fragment = new ChatScreen();

                fragment.setArguments(args);

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragment, fragment);
                transaction.commit();

            }
            Log.i("test", "men her");
        }

    }
}