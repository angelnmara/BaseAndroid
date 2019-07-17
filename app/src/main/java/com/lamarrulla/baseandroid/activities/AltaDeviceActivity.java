package com.lamarrulla.baseandroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lamarrulla.baseandroid.MainActivity;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.fragments.MyDispositivosRecyclerViewAdapter;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.utils.FirebaseAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AltaDeviceActivity extends AppCompatActivity {

    private static final String TAG = "DispositivosFragment";
    private static final String TAGADSF = "AltaDispositivoScanFragment";
    private TextView mTextMessage;
    private int mColumnCount = 1;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    FirebaseAPI firebaseAPI = new FirebaseAPI();
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    List<Dispositivo.DispositivoUsuario> listDispositivoUsuario;
    RecyclerView.Adapter adapter;
    Context context = AltaDeviceActivity.this;
    CoordinatorLayout lnlAltaMAC;
    LinearLayout lnlAltaScan;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceView cameraView;
    private String token = "";
    private String tokenanterior = "";
    BottomNavigationView bottomNavigationView;

    public interface OnItemClickListener {
        void onItemClick(Dispositivo.DispositivoUsuario item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("MissingPermission")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mac_address:
                    lnlAltaScan.setVisibility(View.GONE);
                    lnlAltaMAC.setVisibility(View.VISIBLE);
                    cameraSource.stop();
                    return true;
                case R.id.codigo_qr:
                    try {
                        lnlAltaMAC.setVisibility(View.GONE);
                        lnlAltaScan.setVisibility(View.VISIBLE);
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                /*case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;*/
            }
            return false;
        }
    };

    private void cargaCamara() {
        barcodeDetector = new BarcodeDetector
                .Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(context, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        cameraView = findViewById(R.id.camera_view);
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(AltaDeviceActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                        return;
                    }else{
                        cameraSource.start(cameraView.getHolder());
                    }
                }catch (Exception ex){
                    Log.d(TAG, ex.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    if(!token.isEmpty()){
                        lnlAltaMAC.post(new Runnable() {
                            @Override
                            public void run() {
                                lnlAltaMAC.setVisibility(View.VISIBLE);
                                lnlAltaScan.setVisibility(View.GONE);
                                createDialog(null, 1);
                                cameraSource.stop();
                            }
                        });
                        //lnlAltaScan.setVisibility(View.GONE);
                        //lnlAltaMAC.setVisibility(View.VISIBLE);
                        //cameraSource.stop();

                    }

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    /*if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.i("token", token);

                        if (URLUtil.isValidUrl(token)) {
                            // si es una URL valida abre el navegador
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(token));
                            startActivity(browserIntent);
                        } else {
                            // comparte en otras apps
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, token);
                            shareIntent.setType("text/plain");
                            startActivity(shareIntent);
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }*/
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-
                    try {
                        cameraSource.start(cameraView.getHolder());
                        Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Se tienen que autorizar los permisos para continuar", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_device);
        lnlAltaScan = (LinearLayout) findViewById(R.id.lnlAltaScan);
        lnlAltaMAC = (CoordinatorLayout) findViewById(R.id.lnlAltaMAC);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.altaDispositvo);

        mTextMessage = (TextView) findViewById(R.id.message);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final RecyclerView recyclerView = findViewById(R.id.list);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            //final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mFirebaseAuth = FirebaseAuth.getInstance();

            listDispositivoUsuario = new ArrayList();
            Query query = mDatabase.child("dispositivos").child(mFirebaseAuth.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot du : dataSnapshot.getChildren()){
                            Log.d(TAG, du.getValue().toString());
                            Dispositivo.DispositivoUsuario dispositivoUsuario = du.getValue(Dispositivo.DispositivoUsuario.class);
                            listDispositivoUsuario.add(dispositivoUsuario);
                        }

                    /*try {
                        Gson gso = new Gson();
                        String s1 = gso.toJson(dataSnapshot.getValue());
                        JSONObject jso = new JSONObject(s1);
                        Log.d(TAG, jso.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    }
                    adapter = new MyDispositivosRecyclerViewAdapter(listDispositivoUsuario, new OnItemClickListener(){
                        @Override
                        public void onItemClick(Dispositivo.DispositivoUsuario item) {
                            Log.d(TAG, "");
                            createDialog(item, 2);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        FloatingActionButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(null, 1);
            }
        });
        cargaCamara();
    }

    public void createDialog(final Dispositivo.DispositivoUsuario item, final int opcion){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mViewAgregar = getLayoutInflater().inflate(R.layout.fragment_alta_dispositivo, null);
        mBuilder.setView(mViewAgregar);
        final AlertDialog dialog = mBuilder.create();
        final EditText txtMacAddres = mViewAgregar.findViewById(R.id.txtMacAddres);
        final EditText txtUsuario = mViewAgregar.findViewById(R.id.txtUsuario);
        if(item!=null){
            txtMacAddres.setText(item.dispositivo);
        }
        //txtMacAddres.addTextChangedListener(new MaskWatcher("##:##:##:##:##:##"));
        if(token!=null){
            txtMacAddres.setText(token);
        }
        Button btnAgregar = mViewAgregar.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opcion == 2){
                    listDispositivoUsuario.remove(item);
                }
                Date date = new Date();
                listDispositivoUsuario.add(new Dispositivo.DispositivoUsuario(
                        txtMacAddres.getText().toString().toUpperCase(),
                        txtUsuario.getText().toString().toUpperCase(),
                        true,
                        date,
                        null
                ));
                adapter.notifyItemInserted(listDispositivoUsuario.size() - 1);
                adapter.notifyDataSetChanged();
                dialog.hide();
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myIntent, 0);
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        firebaseAPI.writeNewObject(getString(R.string.dispositivos), listDispositivoUsuario);
    }
}