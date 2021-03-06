package com.lamarrulla.baseandroid.utils;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;*/
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
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
import java.io.FileDescriptor;
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

    /*private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;*/

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

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

    public void OpenAltaUsuario(Context context) {
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
        BitMatrix bitMatrix = multiFormatWriter.encode(getMacAddress(), BarcodeFormat.QR_CODE, 200, 200);
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

    public Bitmap getBitmapFromUri(Uri uri, Context context) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public String getDataQRfromImage(Bitmap bitmap, Context context) {
        String valorDevuelto = "";
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .build();
        /*if(barcode.isOperational()){*/
        SparseArray<Barcode> sparseArray = barcodeDetector.detect(frame);
        if (sparseArray != null && sparseArray.size() > 0) {
            for (int i = 0; i < sparseArray.size(); i++) {
                Log.d(TAG, "Value: " + sparseArray.valueAt(i).rawValue + "----" + sparseArray.valueAt(i).displayValue);
                valorDevuelto += sparseArray.valueAt(i).displayValue;
                //Toast.makeText(context, sparseArray.valueAt(i).rawValue, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "SparseArray null or empty");
        }
        return valorDevuelto;
    }

    /*public void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }*/

    public boolean isConnectAvailable(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

        return connected;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    }

    @SuppressLint("MissingPermission")
    public String getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public String numberToPhone(String number) {
        if(number.length()>0) {
            return String.format("%s %s %s %s", number.substring(0,2), number.substring(2,5), number.substring(5,8), number.substring(8, 12));
            //return number.replace( /\D+/g, "" ).replace( /([0-9]{1,3})([0-9]{3})([0-9]{4}$)/gi, "($1) $2-$3" ); //mask numbers (xxx) xxx-xxxx
        } else {
            return "";
        }
    }
    public void newMark(GoogleMap gmap, LatLng latLng, String title){
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        gmap.addMarker(new MarkerOptions().position(latLng)
                .title(title));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }
}
