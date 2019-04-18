package com.lamarrulla.baseandroid.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReadService extends Service {

    private final String TAG = ReadService.class.getSimpleName();
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    TimerTask timerTask;
    List<Dispositivo.DispositivoUsuario> ListDispositivoUsuario = new ArrayList<Dispositivo.DispositivoUsuario>();

    public ReadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "servicio creado");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "servicio iniciado");
        ListDispositivoUsuario = new ArrayList<Dispositivo.DispositivoUsuario>();
        Query query = mDatabase.child("dispositivos").child(mFirebaseAuth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot du : dataSnapshot.getChildren()){
                        Log.d(TAG, du.getValue().toString());
                        Dispositivo.DispositivoUsuario dispositivoUsuario = du.getValue(Dispositivo.DispositivoUsuario.class);
                        ListDispositivoUsuario.add(dispositivoUsuario);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "No regresaron datos desde firebase");
            }
        });
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                for (final Dispositivo.DispositivoUsuario du:ListDispositivoUsuario
                ) {
                    Log.d(TAG, du.dispositivo);
                    Query queryLatLong = mDatabase.child(getString(R.string.Locations)).child(du.dispositivo);
                    queryLatLong.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                try {
                                    Gson gso = new Gson();
                                    String s1 = gso.toJson(dataSnapshot.getValue());
                                    JSONObject jso = new JSONObject(s1);
                                    Log.d(TAG, jso.toString());
                                    Intent localIntent = new Intent(Constants.ACTION_RUN_SERVICE)
                                            .putExtra(Constants.EXTRA_MEMORY, jso.getString("latitude"));
                                    // Emitir el intent a la actividad
                                    LocalBroadcastManager.getInstance(ReadService.this).sendBroadcast(localIntent);
                                    //LatLng sydney = new LatLng(jso.getDouble("latitude"), jso.getDouble("longitude"));
                                    //gmap.addMarker(new MarkerOptions().position(sydney).title(du.dispositivo));
                                    //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "Ocurrio un error al consultar la base de datos");
                        }
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
        Intent localIntent = new Intent(Constants.ACTION_MEMORY_EXIT);

        // Emitir el intent a la actividad
        LocalBroadcastManager
                .getInstance(ReadService.this)
                .sendBroadcast(localIntent);
        Log.d(TAG, "Servicio destruido...");
    }

        private void responseLocation() {
        ListDispositivoUsuario = new ArrayList<Dispositivo.DispositivoUsuario>();
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        Query query = mDatabase.child("dispositivos").child(mFirebaseAuth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot du : dataSnapshot.getChildren()){
                        Log.d(TAG, du.getValue().toString());
                        Dispositivo.DispositivoUsuario dispositivoUsuario = du.getValue(Dispositivo.DispositivoUsuario.class);
                        ListDispositivoUsuario.add(dispositivoUsuario);
                    }
                    for (final Dispositivo.DispositivoUsuario du:ListDispositivoUsuario
                    ) {
                        Log.d(TAG, du.dispositivo);
                        Query queryLatLong = mDatabase.child(getString(R.string.Locations)).child(du.dispositivo);
                        queryLatLong.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    try {
                                        Gson gso = new Gson();
                                        String s1 = gso.toJson(dataSnapshot.getValue());
                                        JSONObject jso = new JSONObject(s1);
                                        Log.d(TAG, jso.toString());
                                        LatLng sydney = new LatLng(jso.getDouble("latitude"), jso.getDouble("longitude"));
                                        //gmap.addMarker(new MarkerOptions().position(sydney).title(du.dispositivo));
                                        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "Ocurrio un error al consultar la base de datos");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
