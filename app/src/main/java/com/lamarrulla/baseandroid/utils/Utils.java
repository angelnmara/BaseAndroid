package com.lamarrulla.baseandroid.utils;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.lamarrulla.baseandroid.MainActivity;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.activities.AltaUsuarioActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Utils {

    public Bitmap getMyBitmap() {
        return myBitmap;
    }

    private Bitmap myBitmap;

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
    public void showProgress(final View ShowView, final View HiddenView, Context context) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            ShowView.setVisibility(View.VISIBLE);
            ShowView.animate().setDuration(shortAnimTime).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ShowView.setVisibility(View.VISIBLE);
                }
            });

            HiddenView.setVisibility(View.GONE);
            HiddenView.animate().setDuration(shortAnimTime).alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    HiddenView.setVisibility(View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            HiddenView.setVisibility(View.GONE);
            ShowView.setVisibility(View.VISIBLE);
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

    public void OpenAltaUsuario(Context context){
        FirebaseAPI firebaseAPI = new FirebaseAPI();
        firebaseAPI.writeNewUser();
        Intent intent = new Intent(context, AltaUsuarioActivity.class);
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
                macAddress = buf.toString().toLowerCase();
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        } // for now eat exceptions
    }

    public Bitmap getQR(Context context) throws WriterException {
        Bitmap bitmap;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        getMAC(context);
        BitMatrix bitMatrix = multiFormatWriter.encode(getMacAddress(), BarcodeFormat.QR_CODE,200,200);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        bitmap = barcodeEncoder.createBitmap(bitMatrix);
        return bitmap;
    }

    public File createFile(Bitmap myBitmap, Context context) throws IOException {
        //create a file to write bitmap data
        File f = new File(context.getExternalCacheDir(), "myQR.PNG");
        //getCacheDir()
        f.createNewFile();

        //Convert bitmap to byte array
        Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 500, 500, false);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }

    public void requestPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    public boolean isConnectAvailable(Context context){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }
}
