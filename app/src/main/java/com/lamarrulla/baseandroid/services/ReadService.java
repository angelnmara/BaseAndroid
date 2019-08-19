package com.lamarrulla.baseandroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
/*import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;*/
import android.util.Log;
/*import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;*/
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/*import java.util.HashMap;*/
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReadService extends Service {

    private final String TAG = ReadService.class.getSimpleName();
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    TimerTask timerTask;
    Timer timer = new Timer();
    /*List<Dispositivo.DispositivoUsuario> ListDispositivoUsuario = new ArrayList<Dispositivo.DispositivoUsuario>();*/

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
        try{
            Log.d(TAG, "servicio iniciado");
            Bundle extras = intent.getExtras();
            final JSONArray jsa = new JSONArray(extras.getString("listaDispositivos"));
            //final HashMap<String, Marker> markersAndObjects = (HashMap<String, Marker>)intent.getSerializableExtra("listaDispositivos");
            //timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    for(int i=0;i<jsa.length();i++){
                        try {
                            JSONObject jso = jsa.getJSONObject(i);
                            if(jso.has("valor")){
                                if(jso.getBoolean("valor")){
                                    final String dispositivoJSO =jso.getString("dispositivo").toUpperCase();
                                    //Log.d(TAG, dispositivoJSO);
                                    Query queryLatLong = mDatabase.child(getString(R.string.Locations)).child(dispositivoJSO);
                                    queryLatLong.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                try {
                                                    Gson gso = new Gson();
                                                    String s1 = gso.toJson(dataSnapshot.getValue());
                                                    JSONObject jso = new JSONObject(s1);
                                                    if(jso.has("latitude") && jso.has("longitude")){
                                                        String _latitude = jso.getString(getString(R.string.latitude));
                                                        String _longitude = jso.getString(getString(R.string.longitude));
                                                        String _bearing = jso.getString(getString(R.string.bearing));
                                                        String _speed = jso.getString(getString(R.string.speed));
                                                        Log.d(TAG, "recive " + _longitude+ " - " + _latitude);
                                                        Intent localIntent = new Intent(Constants.ACTION_RUN_SERVICE)
                                                                .putExtra(Constants.LATITUD, _latitude)
                                                                .putExtra(Constants.LONGITUD, _longitude)
                                                                .putExtra(Constants.DISPOSITIVO, dispositivoJSO)
                                                                .putExtra(Constants.BEARING, _bearing)
                                                                .putExtra(Constants.SPEED, _speed);
                                                        // Emitir el intent a la actividad
                                                        LocalBroadcastManager.getInstance(ReadService.this).sendBroadcast(localIntent);
                                                        //LatLng sydney = new LatLng(jso.getDouble("latitude"), jso.getDouble("longitude"));
                                                        //gmap.addMarker(new MarkerOptions().position(sydney).title(du.dispositivo));
                                                        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                //timer.cancel();
                                                //stopSelf();
                                                Log.d(TAG, "datasnapshot dispositivo no existe: " + dispositivoJSO);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d(TAG, "Ocurrio un error al consultar la base de datos");
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 5000);
        }catch(Exception ex){
            stopSelf();
            Log.d(TAG, ex.getMessage());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.purge();
        timer.cancel();
        //timerTask.cancel();
        stopSelf();
        Intent localIntent = new Intent(Constants.ACTION_MEMORY_EXIT);

        // Emitir el intent a la actividad
        LocalBroadcastManager
                .getInstance(ReadService.this)
                .sendBroadcast(localIntent);
        Log.d(TAG, "Servicio destruido...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
