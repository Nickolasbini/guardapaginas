package com.br.guardapaginas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class TheInitialScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_initial_screen);

        getSupportActionBar().hide();

        new android.os.Handler(Looper.getMainLooper()).postDelayed(
            new Runnable() {
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                    intent.putExtra("SPLASH_SCREEN_EXECUTED", "1");
                    startActivity(intent);
                }
            },
3000);
    }
}