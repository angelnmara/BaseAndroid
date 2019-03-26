package com.lamarrulla.baseandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private final String splash = "SplashActivity";
    boolean isLoggedInFacebook = false;
    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        /* valida si ya esta logeado con facebook */
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedInFacebook = accessToken != null && !accessToken.isExpired();
        /* valida si ya esta logeado con facebook */

        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                validaToken();
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void validaToken(){
        if(isLoggedInFacebook){
            mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        }else{
            mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
        }
    }
}
