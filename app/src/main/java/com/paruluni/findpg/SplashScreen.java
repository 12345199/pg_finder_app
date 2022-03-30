package com.paruluni.findpg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends Activity {

    SharedPreferences shared;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        shared = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = shared.edit();
        //boolean firstTime = shared.getBoolean("first", true);
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();


    }
}

