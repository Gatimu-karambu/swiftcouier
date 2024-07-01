package com.swift.swiftcourier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_FIRST_TIME = "firstTime";
    LottieAnimationView lottieAnimationView;
    private View lottieOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slapsh_screen);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(KEY_FIRST_TIME, true);

        lottieAnimationView=findViewById(R.id.lottieAnimationView);
        lottieOverlay = findViewById(R.id.lottieOverlay);

        lottieOverlay.setVisibility(View.VISIBLE);

        lottieAnimationView.playAnimation();


        if (isFirstTime) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lottieOverlay.setVisibility(View.GONE);

                    lottieAnimationView.cancelAnimation();
                    Intent intent = new Intent(SplashActivity.this,  MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 5000);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lottieOverlay.setVisibility(View.GONE);

                    lottieAnimationView.cancelAnimation();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }




    }
}