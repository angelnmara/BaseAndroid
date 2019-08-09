package com.lamarrulla.baseandroid;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
//import com.google.gson.JsonElement;
import com.google.zxing.WriterException;
import com.lamarrulla.baseandroid.activities.AltaDeviceActivity;
import com.lamarrulla.baseandroid.activities.TrackerActivity;
import com.lamarrulla.baseandroid.fragments.AltaDispositivoFragment;
import com.lamarrulla.baseandroid.fragments.GeneraCodigoFragment;
import com.lamarrulla.baseandroid.implement.Acceso;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.services.ReadService;
import com.lamarrulla.baseandroid.utils.Constants;
import com.lamarrulla.baseandroid.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AltaDispositivoFragment.OnFragmentAltaDispositivoInteractionListener,
        OnMapReadyCallback {

    IAcceso iAcceso = new Acceso();
    Context context = this;
    private View mProgressView;
    private View mPrincipalSV;
    NavigationView navigationView;
    private getMenu mAuthTask = null;
    Utils utils = new Utils();
    SharedPreferences sharedPreferences;
    private int TipoAcceso;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
    private static final String altadispositivofragment = "AltaDispositivoFragment";
    private static final String dispositivosfragment = "DispositivosFragment";

    //private MapView mapView;
    private GoogleMap gmap;
    //public static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private FusedLocationProviderClient fusedLocationClient;
    JSONArray jsaDispositivos = new JSONArray();

    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;

    Toolbar toolbar;

    Intent intentReadService;
    Intent intentTrackerServices;

    MarkerOptions mo;
    Marker marker;
    HashMap<String, Marker> markersAndObjects;
    List<Dispositivo.DispositivosMarks> listDispositivosMarks;

    //Bitmap Icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "OnCreate");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        CambiosEnToolBar();

        //mProgressView = findViewById(R.id.main_progress);
        //mPrincipalSV = findViewById(R.id.svPrincipal);

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        intentReadService = new Intent(getApplicationContext(), ReadService.class);

        /*  inicia localizacion */
        intentTrackerServices = new Intent(MainActivity.this, TrackerActivity.class);
        /*  inicia localizacion */


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyLocation();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*  Carga valores de usuario en menu    */

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);

        //final ImageView imgView = header.findViewById(R.id.imageView);
        if(user.getDisplayName()!=null){
            TextView textView = header.findViewById(R.id.txtHeaderName);
            textView.setText(user.getDisplayName());
        }
        if(user.getPhotoUrl()!=null){
            new DownloadImageTask((ImageView) header.findViewById(R.id.imageView)).execute(user.getPhotoUrl().toString());
        }
        if(user.getEmail()!=null){
            TextView textViewCorreo = header.findViewById(R.id.txtHeaderCorreo);
            textViewCorreo.setText(user.getEmail());
        }

        /*  Fin Carga valores de usuario en menu    */

        /*  Cambia icono del menu   */
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        /*  Cambia icono del menu   */

        /*  Selecciona el tipo de acceso    */
        TipoAcceso = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.TipoAcceso), "2"));

        if (TipoAcceso == 1) {
            /*  obtine modulo desde servidor    */
            /*  select menu */
            showProgress(true);
            mAuthTask = new getMenu(context, "tbmodulo");
            mAuthTask.execute((Void) null);
            /*  select menu */
        }
        /*  Selecciona el tipo de acceso    */

        navigationView.setNavigationItemSelectedListener(this);

        /*maps*/
        //setContentView(R.layout.content_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.lnlPrincipalFragment);

        mapFragment.getMapAsync(this);
        /*maps*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        Log.d(TAG, "onPrepareOptionMenu" + jsaDispositivos.length());
        Log.d(TAG, jsaDispositivos.toString());
        menu.clear();
        for(int i = 0; i<jsaDispositivos.length();i++){
            try {
                JSONObject jso = jsaDispositivos.getJSONObject(i);
                Log.d(TAG, jso.toString());

                if(jso.has("activo")) {
                    if (jso.getBoolean("activo")) {
                        if (jso.has("valor")) {
                            menu.add(0, i, Menu.FLAG_PERFORM_NO_CLOSE, jso.getString("usuario")).setChecked(jso.getBoolean("valor"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //menu.add(0,1, Menu.CATEGORY_ALTERNATIVE, "primero").setChecked(true);
        //menu.add(0,2, Menu.CATEGORY_ALTERNATIVE, "primero2");
        menu.setGroupCheckable(0, true, false);
        return super.onPrepareOptionsMenu(menu);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage){
            this.bmImage = bmImage;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String urldisplay = strings[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result){
            try{
                Log.d(TAG, "result: " + result);
                if(result!=null){
                    Resources resources = getResources();
                    RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(resources, result);
                    dr.setCornerRadius(Math.max(result.getWidth(), result.getHeight())/2.0f);
                    //bmImage.setImageBitmap(Bitmap.createScaledBitmap(result, 120, 120, false));
                    bmImage.setImageDrawable(dr);
                }
            }catch (Exception ex){
                Log.d(TAG, ex.getMessage());
            }

        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText(context, getString(R.string.activarGPS), Toast.LENGTH_SHORT).show();
        }
        else{
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                            }
                        }
                    });
        }
    }

    public void getListaDispositivos(final String dispositivo, final String usuario) {
        listDispositivosMarks = new ArrayList<>();
            Query queryLatLong = mDatabase.child(getString(R.string.Locations)).child(dispositivo);
            queryLatLong.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        try {
                            Gson gso = new Gson();
                            String s1 = gso.toJson(dataSnapshot.getValue());
                            JSONObject jso = new JSONObject(s1);
                            Log.d(TAG, jso.toString());
                            if(jso.has("latitude") && jso.has("longitude")){
                                Double lati = Double.parseDouble(jso.getString("latitude"));
                                Double longi = Double.parseDouble(jso.getString("longitude"));
                                LatLng sydney = new LatLng(lati, longi);
                                mo = new MarkerOptions()
                                        .position(sydney)
                                        .title(usuario)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_car))
                                        .anchor(0.5f,0.5f)
                                        .zIndex(1.0f);
                                marker = gmap.addMarker(mo);
                                Dispositivo.DispositivosMarks dispositivosMarks = new Dispositivo.DispositivosMarks();
                                dispositivosMarks.dispositivo = dispositivo;
                                dispositivosMarks.marker = marker;
                                listDispositivosMarks.add(dispositivosMarks);
                                Log.d(TAG, "se guardan marks");
                            }
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

    private void ejecutaIntent(String jsa){
        intentReadService.putExtra("listaDispositivos", jsa);
        startService(intentReadService);
        // Filtro de acciones que serán alertadasObject HashMap
        IntentFilter filter = new IntentFilter(Constants.ACTION_RUN_ISERVICE);
        filter.addAction(Constants.ACTION_RUN_SERVICE);
        filter.addAction(Constants.ACTION_MEMORY_EXIT);
        filter.addAction(Constants.ACTION_PROGRESS_EXIT);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver,
                filter);
    }

    private class ResponseReceiver extends BroadcastReceiver{

        // Sin instancias
        private ResponseReceiver() {
        }

        /*Regresa información desde el servicio*/
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_RUN_SERVICE:
                    try{
                        String dispositivo = intent.getStringExtra(Constants.DISPOSITIVO);
                        Double latitud = Double.parseDouble(intent.getStringExtra(Constants.LATITUD));
                        Double longitud = Double.parseDouble(intent.getStringExtra(Constants.LONGITUD));
                        Double speed = Double.parseDouble(intent.getStringExtra(Constants.SPEED));
                        Integer bearing = speed==0?0:Integer.parseInt(intent.getStringExtra(Constants.BEARING)) + 90;
                        Log.d(TAG, "On reciver: " + dispositivo + " - " + longitud + " - " + latitud);
                        LatLng latLng = new LatLng(latitud, longitud);
                        if(listDispositivosMarks!= null){
                            for (Dispositivo.DispositivosMarks dispositivoMarks: listDispositivosMarks
                            ) {
                                if(dispositivoMarks.dispositivo.equals(dispositivo)){
                                    dispositivoMarks.marker.setPosition(latLng);
                                    dispositivoMarks.marker.setRotation(bearing);
                                }
                            }
                        }
                    }catch (Exception ex){
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.ACTION_RUN_ISERVICE:
                    Log.d(TAG, intent.getStringExtra(Constants.EXTRA_PROGRESS));
                    break;

                case Constants.ACTION_MEMORY_EXIT:
                    Log.d(TAG, "memoria");
                    break;

                case Constants.ACTION_PROGRESS_EXIT:
                    Log.d(TAG, "progreso");
                    break;
            }
        }
    }

    public void CambiosEnToolBar() {
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentAltaDispositivoInteraction(Uri uri) {
        Log.d(TAG, "interaccion de fragment");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.getUiSettings().setMapToolbarEnabled(false);
        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Toast.makeText(context, "click map", Toast.LENGTH_SHORT).show();
            }
        });
        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*Meter aqui el evento para hacer la ruta*/
                //Toast.makeText(context, "click on marker", Toast.LENGTH_LONG).show();
                Double latitude = marker.getPosition().latitude;
                Double longitude = marker.getPosition().longitude;
                LatLng latLng = new LatLng(latitude, longitude);
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions();
            return;
        }else{
            try {
                iniciaTodo();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void iniciaTodo() throws JSONException {
        gmap.setMyLocationEnabled(true);
        setMap();
        getMyLocation();
        startActivity(intentTrackerServices);
        getDispositivos();
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @SuppressLint("MissingPermission")
    public void setMap() {
        gmap.setMinZoomPreference(6.0f);
        gmap.setMaxZoomPreference(18.0f);
        gmap.getUiSettings().setCompassEnabled(false);
        gmap.getUiSettings().setMyLocationButtonEnabled(false);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        //getMyLocation();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-
                    Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
                    try {
                        iniciaTodo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, e.getMessage());
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Se tienen que autorizar los permisos para continuar", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public class getMenu extends AsyncTask<Void, Void, Boolean> {

        private final Context mContext;
        private final String mTabla;

        getMenu(Context context, String tabla) {
            mContext = context;
            mTabla = tabla;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                iAcceso.setContext(context);
                iAcceso.setTabla(mTabla);
                iAcceso.ejecutaSelect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return iAcceso.getEsCorrecto();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            if (success) {
                try {
                    JSONObject jso = iAcceso.getJso();
                    JSONArray jsa = jso.getJSONArray("tbmodulo");
                    ArrayList<Login.Menu> menuList = new ArrayList<Login.Menu>();
                    for (int i = 0; i < jsa.length(); i++) {
                        JSONObject jsoM = jsa.getJSONObject(i);
                        menuList.add(new Login.Menu(jsoM.getString("fcmodulo"), jsoM.getString("fcmoduloimg"), i));
                    }
                    agregaMenu(navigationView, menuList);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                System.out.println("post fail");
                //mPrincipalSV.requestFocus();
                showProgress(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public void agregaMenu(NavigationView navigationView, List<Login.Menu> menuList) {
        try {
            /* Obten menu */
            Menu menu = navigationView.getMenu();
            /* Limpia menu */
            menu.clear();
            if (!menuList.isEmpty()) {
                for (Login.Menu m : menuList
                ) {
                    menu.add(1, m.ordenMenu, m.ordenMenu, m.nombreMenu).setIcon(utils.getResourceDrawforName(m.imagenMenu));
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Boolean valor = (item.isChecked()?false:true);

        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

        try {

            JSONObject jso = jsaDispositivos.getJSONObject(item.getItemId());
            if(jso.has("valor")){
                //jso.remove("valor");
                jso.put("valor", valor);
            }
            removeMarks();
            getDispositivos();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            /*Toast.makeText(this, "alta dispositivo", Toast.LENGTH_SHORT).show();*/
            Intent intent = new Intent(this, AltaDeviceActivity.class);
            startActivity(intent);
        }
        /*else if (id == R.id.nav_ubicacion) {
            startActivity(new Intent(MainActivity.this, c.class));
            *//*} else if (id == R.id.nav_manage) {*//*

        }*/
        else if(id == R.id.nav_share){
            //Toast.makeText(context, "Compartir", Toast.LENGTH_LONG).show();
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("image/jpg");
            final File photoFile;
            try {
                photoFile = utils.createFile(utils.getQR(context), context);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                startActivity(Intent.createChooser(shareIntent, "Share image using"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }else if (id == R.id.nav_manage) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.lnlPrincipalFragment, new GeneraCodigoFragment(), "GeneraCodigoFragment").addToBackStack("GeneraCodigoFragment").commit();

        } else if (id == R.id.nav_exit) {
            salir();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void salir() {
        stopService(intentReadService);
        switch (TipoAcceso) {
            case 1:
            case 2:
                salirFirebase();
                break;
            case 3:
                salirFirebase();
                LoginManager.getInstance().logOut();
                break;
            case 4:
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseAuth.getInstance().signOut();
                                regresaLogin();
                            }
                        });
                break;
            default:
                Toast.makeText(context, "opcion invalida", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void salirFirebase() {
        FirebaseAuth.getInstance().signOut();
        regresaLogin();
    }

    private void regresaLogin() {
        stopService(intentReadService);
        removeMarks();
        Log.d(TAG, "Para Servicio");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPrincipalSV.setVisibility(show ? View.GONE : View.VISIBLE);
            mPrincipalSV.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPrincipalSV.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPrincipalSV.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void getMyLocation(){
        Location location = gmap.getMyLocation();
            if(location!=null){
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
        }else{
                getLastLocation();
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopService(intentReadService);
        //removeMarks();
        Log.d(TAG, "Para Servicio");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(intentReadService);
        removeMarks();
        Log.d(TAG, "Para Servicio");
    }

    public void removeMarks(){
        if(listDispositivosMarks != null){
            for (Dispositivo.DispositivosMarks dm: listDispositivosMarks
            ) {
                dm.marker.remove();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startService(intentReadService);
    }

    public void compareJSA(JSONArray jsa1, JSONArray jsa2) throws JSONException {
        if(jsa1.length()==jsa2.length()){
            for(int i = 0; i< jsa1.length(); i++){
                JSONObject jso1 = jsa1.getJSONObject(i);
                JSONObject jso2 = jsa2.getJSONObject(i);
                jso2.put("valor", jso1.getBoolean("valor"));
            }
        }
        jsaDispositivos = jsa2;
    }

    public void getDispositivos() {
            Log.d(TAG, "Obtiene Dispositivos:" + jsaDispositivos.length());
                Query query = mDatabase.child("dispositivos").child(mFirebaseAuth.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "dataSnapshot.exists():" + dataSnapshot.exists());
                        if(dataSnapshot.exists()){
                            try {
                                Gson gso = new Gson();
                                String s1 = gso.toJson(dataSnapshot.getValue());
                                JSONArray jsaTemp = new JSONArray(s1);
                                compareJSA(jsaDispositivos, jsaTemp);
                                /*inicia servicio*/
                                Log.d(TAG, jsaDispositivos.toString());
                                Log.d(TAG, "Inicia Servicio");
                                iniciaServicioDispositivos();
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "No regresaron datos desde firebase");
                    }
                });
    }
    public void iniciaServicioDispositivos() throws JSONException {
        Log.d(TAG, "iniciaServicioDispositivos");
        for(int i = 0; i<jsaDispositivos.length();i++){
            JSONObject jso = jsaDispositivos.getJSONObject(i);
            if(!jso.has("valor")){
                jso.put("valor", jso.getBoolean("activo"));
            }
            if(jso.getBoolean("valor")){
                getListaDispositivos(jso.getString("dispositivo"), jso.getString("usuario"));
            }
        }
        ejecutaIntent(jsaDispositivos.toString());
        invalidateOptionsMenu();
    }
}