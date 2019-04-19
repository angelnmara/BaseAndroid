package com.lamarrulla.baseandroid.utils;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.lamarrulla.baseandroid.MainActivity;
import com.lamarrulla.baseandroid.R;

import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static final String TAG = "Utils";

    private String macAddress;

    public String getMacAddress() {
        return macAddress;
    }

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    public void guardaShared(Activity activity, int variable, String valor) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(variable), valor);
        editor.commit();
    }

    public void removeShared(Activity activity, int variable) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().remove(activity.getString(variable)).commit();
    }

    public int getResourceDrawforName(String drawableName) {
        int drawableId = 0;
        try {
            Class res = R.drawable.class;
            Field field = res.getField(drawableName);
            drawableId = field.getInt(null);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return drawableId;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View ShowView, final View HiddenView, Context context) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            ShowView.setVisibility(show ? View.GONE : View.VISIBLE);
            ShowView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ShowView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            HiddenView.setVisibility(show ? View.VISIBLE : View.GONE);
            HiddenView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    HiddenView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            HiddenView.setVisibility(show ? View.VISIBLE : View.GONE);
            ShowView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void OpenMain(Context context) {
        FirebaseAPI firebaseAPI = new FirebaseAPI();
        firebaseAPI.writeNewUser();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public void getMAC(Context context) {
        try {
            String interfaceName = context.getString(R.string.wlan0);
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return;
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                macAddress = buf.toString();
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        } // for now eat exceptions
    }

    public void requestPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
}
