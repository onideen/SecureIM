package edu.ucsb.cs290g.secureim;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by arnbju on 6/9/13.
 */
public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";

    private final int FRAGMENT_COUNT = 1;
    private final int FIND_USER = 0;

    private Fragment fragments[] = new Fragment[FRAGMENT_COUNT];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        FragmentManager fm = getFragmentManager();

        Fragment fragment = new Login();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();

    }


}



