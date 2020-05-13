package com.example.dchinch.snapsnack;

/**
 * Created by dchinch on 4/2/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

          @Override
          public void run() {
            // This method will be executed once the timer is over

            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);

            finish();
          }
        }, SPLASH_TIME_OUT);
      }
    }
