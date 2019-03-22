package com.lamarrulla.baseandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private final String splash = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                validaToken();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void validaToken(){
        Intent mainIntent = new Intent(Splash.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Splash.this.startActivity(mainIntent);
        Splash.this.finish();
    }
}
