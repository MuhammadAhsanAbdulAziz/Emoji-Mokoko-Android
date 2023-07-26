package com.example.emojimokoko;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {

            Intent Nexscreen=new Intent(Splash_screen.this,MainActivity.class);
            startActivity(Nexscreen);
            finish();
        }
    },3000);
    }
}