package com.lamarrulla.baseandroid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
/*import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;*/
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lamarrulla.baseandroid.MainActivity;
import com.lamarrulla.baseandroid.R;

import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.models.Dispositivo.DispositivoUsuario;
import com.lamarrulla.baseandroid.utils.FirebaseAPI;
import com.lamarrulla.baseandroid.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AltaDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DispositivosFragment";
    private static final String TAGADSF = "AltaDispositivoScanFragment";
    private TextView mTextMessage;
    private int mColumnCount = 1;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    FirebaseAPI firebaseAPI = new FirebaseAPI();
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    //List<DispositivoUsuario> listDispositivoUsuario;
    RecyclerView.Adapter adapter;
    Context context = AltaDeviceActivity.this;
    CoordinatorLayout lnlAltaMAC;
    LinearLayout lnlAltaScan;
    RelativeLayout lnlSinConexion;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceView cameraView;
    //private String token = "";
    //private String tokenanterior = "";
    BottomNavigationView bottomNavigationView;
    Utils utils = new Utils();
    Button btnReintentar;
    RecyclerView recyclerView;
    AlertDialog dialog;
    private static final int READ_REQUEST_CODE = 42;
    String valorMAC;

    List<DispositivoUsuario> dispUsuList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReintentar:
                //Toast.makeText(context, "Click btnReintentar", Toast.LENGTH_SHORT).show();
                if(utils.isConnectAvailable(context)){
                    try {
                        seleccionaEscanea();
                        bottomNavigationView.setSelectedItemId(R.id.codigo_qr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(context, getString(R.string.noConexionInternet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAgregar:
                //createDialog(null, 1);
                /*Toast.makeText(context, "Boton agregar", Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    Bitmap bitmap = utils.getBitmapFromUri(uri, context);
                    valorMAC = utils.getDataQRfromImage(bitmap, context);
                    //Toast.makeText(context, valorMAC, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //showImage(uri);
                DispositivoUsuario item = new DispositivoUsuario();
                item.dispositivo = valorMAC;
                createDialog(item, 1);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DispositivoUsuario item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (!utils.isConnectAvailable(context)) {
                lnlSinConexion.setVisibility(View.VISIBLE);
                lnlAltaMAC.setVisibility(View.GONE);
                lnlAltaScan.setVisibility(View.GONE);
                return false;
            }

            switch (item.getItemId()) {
                case R.id.mac_address:
                    lnlAltaScan.setVisibility(View.GONE);
                    lnlAltaMAC.setVisibility(View.VISIBLE);
                    lnlSinConexion.setVisibility(View.GONE);
                    cameraSource.stop();
                    getSupportActionBar().setTitle(R.string.mis_dispositivos);
                    return true;
                case R.id.codigo_qr:
                    try {
                        seleccionaEscanea();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
            //return false;
        }
    };

    @SuppressLint("MissingPermission")
    private void seleccionaEscanea() throws IOException {
        lnlAltaMAC.setVisibility(View.GONE);
        lnlAltaScan.setVisibility(View.VISIBLE);
        lnlSinConexion.setVisibility(View.GONE);
        cameraSource.start(cameraView.getHolder());
        getSupportActionBar().setTitle(R.string.escaneaQR);
    }

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
                    final String token = barcodes.valueAt(0).displayValue.toString();

                    if(!token.isEmpty()){
                        lnlAltaMAC.post(new Runnable() {
                            @Override
                            public void run() {
                                lnlAltaMAC.setVisibility(View.VISIBLE);
                                lnlAltaScan.setVisibility(View.GONE);
                                DispositivoUsuario item = new DispositivoUsuario();
                                item.dispositivo = token.toString();
                                createDialog(item, 1);
                                cameraSource.stop();
                            }
                        });
                    }
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
        lnlSinConexion = findViewById(R.id.lnlSinConexion);
        btnReintentar = findViewById(R.id.btnReintentar);
        btnReintentar.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.escaneaQR);

        mTextMessage = (TextView) findViewById(R.id.message);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        recyclerView = findViewById(R.id.list);
        FloatingActionButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(this);
        /*btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(null, 1);
            }
        });*/

        cargaDatosLista();
        cargaCamara();

        if(!utils.isConnectAvailable(context)){
            lnlSinConexion.setVisibility(View.VISIBLE);
            lnlAltaMAC.setVisibility(View.GONE);
            lnlAltaScan.setVisibility(View.GONE);
        }
    }

    public void cargaDatosLista(){
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

                //listDispositivoUsuario = new ArrayList();
                Query query = mDatabase.child("dispositivos").child(mFirebaseAuth.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "dataSnapshot.exists():" + dataSnapshot.exists());
                        if(dataSnapshot.exists()){
                            Gson gso = new Gson();
                            String s1 = gso.toJson(dataSnapshot.getValue());TypeToken<List<Dispositivo.DispositivoUsuario>> token = new TypeToken<List<Dispositivo.DispositivoUsuario>>() {};
                            dispUsuList = gso.fromJson(s1, token.getType());
                        }
                        adapter = new com.lamarrulla.baseandroid.adapters.MyDispositivosRecyclerViewAdapter(dispUsuList, new OnItemClickListener(){
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
                        Log.d(TAG, "No regresaron datos desde firebase");
                    }
                });
            }
    }

    public void createDialog(final DispositivoUsuario item, final int opcion){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mViewAgregar = getLayoutInflater().inflate(R.layout.fragment_alta_dispositivo, null);
        mBuilder.setView(mViewAgregar);
        dialog = mBuilder.create();
        final EditText txtMacAddres = mViewAgregar.findViewById(R.id.txtMacAddres);
        final EditText txtUsuario = mViewAgregar.findViewById(R.id.txtUsuario);
        final ImageView btnCerrar = mViewAgregar.findViewById(R.id.btnCerrar);
        final TextView txtAlta = mViewAgregar.findViewById(R.id.txtAltaDispositivo);
        final TextView txtEdita = mViewAgregar.findViewById(R.id.txtEditaDispositivo);
        Button btnAgregar = mViewAgregar.findViewById(R.id.btnAgregar);

        txtMacAddres.setText(item.dispositivo.toUpperCase());

        //  Personaliza dialog
        if(opcion == 1){
            txtEdita.setVisibility(View.GONE);
            txtAlta.setVisibility(View.VISIBLE);
            btnAgregar.setText(getString(R.string.agregar));
        }else{
            txtEdita.setVisibility(View.VISIBLE);
            txtAlta.setVisibility(View.GONE);
            btnAgregar.setText(getString(R.string.actualizar));
            txtUsuario.setText(item.usuario);
        }
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean salida = false;
                // valida si ya existe un usuario con ese nombre, se agrega validación diferente de null por que cuando no existian usuarios tronaba

                    for (DispositivoUsuario du:dispUsuList
                    ) {
                        if(du.usuario.toUpperCase().equals(txtUsuario.getText().toString().toUpperCase())){
                            salida = true;
                        }
                    }

                if(!item.dispositivo.matches(getString(R.string.regxMAC))){
                    Toast.makeText(context, getString(R.string.errorImagenSeleccionada), Toast.LENGTH_LONG).show();
                    dialog.hide();
                    return;
                }else if(txtUsuario.getText().toString().length()<=0){
                    Toast.makeText(context, getString(R.string.usuarioSinNombre), Toast.LENGTH_LONG).show();
                    return;
                }else if(salida){
                    Toast.makeText(context, getString(R.string.usuarioMismoNombre), Toast.LENGTH_LONG).show();
                    return;
                }

                if (opcion == 2){
                    dispUsuList.remove(item);
                }
                //Date date = new Date();
                Dispositivo.Fecha date = new Dispositivo.Fecha();
                dispUsuList.add(new Dispositivo.DispositivoUsuario(
                        txtMacAddres.getText().toString().toUpperCase(),
                        txtUsuario.getText().toString().toUpperCase(),
                        true,
                         date,
                        null,
                        false,
                        false,
                        new LatLng(0, 0)
                ));
                adapter.notifyItemInserted(dispUsuList.size() - 1);
                adapter.notifyDataSetChanged();
                dialog.hide();
                //Toast.makeText(context, "Click agregar", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
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
        if(utils.isConnectAvailable(context)){
            firebaseAPI.writeNewObject(getString(R.string.dispositivos), dispUsuList);
        }else{
            Toast.makeText(context, getString(R.string.noConexionInternet), Toast.LENGTH_SHORT).show();
        }
        if(dialog!=null){
            dialog.dismiss();
        }
    }
}