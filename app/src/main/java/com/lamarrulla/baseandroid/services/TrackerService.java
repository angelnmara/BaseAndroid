package com.lamarrulla.baseandroid.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
/*import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;*/
import android.os.Build;
import android.os.IBinder;
/*import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;*/
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.utils.Utils;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String NOTIFICATION_CHANNEL_ID;
    Utils utils = new Utils();
    Context context = (Context) this;

    public TrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        utils.getMAC(context);
        requestLocationUpdates();
        //loginToFirebase();
    }

    private void buildNotification() {

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NOTIFICATION_CHANNEL_ID = getString(R.string.app_name);
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }

        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_stat_name);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    /*
    private void loginToFirebase() {
        // Authenticate with Firebase, and request location updates
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    requestLocationUpdates();
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }*/

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(7000);
        request.setFastestInterval(5000);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path) + "/" + utils.getMacAddress().toUpperCase();
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "Envia posición " + location.getLongitude() + " - " + location.getLatitude());
                        /*Gson gson = new Gson();
                        String jsonLocation = gson.toJson(location);*/
                        Dispositivo.MyLocation myLocation = new Dispositivo.MyLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAltitude(),
                                location.getElapsedRealtimeNanos(),
                                location.getTime(),
                                location.getBearing(),
                                location.getBearingAccuracyDegrees(),
                                location.getSpeed(),
                                location.getSpeedAccuracyMetersPerSecond(),
                                location.getVerticalAccuracyMeters(),
                                location.getAccuracy()
                        );
                        ref.setValue(myLocation);
                    }
                }
            }, null);
        }
    }
}
