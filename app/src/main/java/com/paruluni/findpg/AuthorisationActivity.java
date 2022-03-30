package com.paruluni.findpg;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.paruluni.findpg.adpter.ViewPagerAdapter;

public class AuthorisationActivity extends AppCompatActivity {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    ViewPagerAdapter viewpageradapter;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorisation);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(AuthorisationActivity.this,"You are already signed in.",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(AuthorisationActivity.this, MainActivity.class));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("LOGIN/SIGN UP");
        setSupportActionBar(toolbar);
        //disabling keyboard when the register activity opens
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.container);
        viewpageradapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpageradapter.addfragment(new LoginFrag(), "Login");
        viewpageradapter.addfragment(new SignUpFrag(), "Signup");
        mViewPager.setAdapter(viewpageradapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }
}
