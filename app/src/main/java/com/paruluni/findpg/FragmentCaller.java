package com.paruluni.findpg;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentCaller extends AppCompatActivity {

    Fragment frag = new BlankFragment();
    FragmentManager manager;


    private static final String TAG = "PgDetailsAdapter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_caller);

        Log.d(TAG, "inside");

        Intent i = getIntent();
        Bundle b = i.getExtras();
        Log.d(TAG, (String) b.get("PG ID"));
        callingFragment(b);
    }

    //This function extracts the pg "information" from the bundle and sends it via newInstance
    public void callingFragment(Bundle pn)
    {
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        frag = BlankFragment.newInstance("PG ID","h" );
        frag.setArguments(pn);
        transaction.replace(R.id.frame,frag,null);
        transaction.commit();
    }

}