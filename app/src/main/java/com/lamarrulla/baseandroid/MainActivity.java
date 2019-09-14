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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
/*import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;*/
import android.util.Log;
import android.view.View;
/*import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;*/
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;
import com.lamarrulla.baseandroid.adapters.MyUsersRecyclerViewAdapter;
import com.lamarrulla.baseandroid.activities.AltaDeviceActivity;
import com.lamarrulla.baseandroid.activities.TrackerActivity;
import com.lamarrulla.baseandroid.fragments.AltaDispositivoFragment;
import com.lamarrulla.baseandroid.fragments.GeneraCodigoFragment;
import com.lamarrulla.baseandroid.adapters.MylistaUbicacionesRecyclerViewAdapter;
import com.lamarrulla.baseandroid.fragments.UsersFragment;
import com.lamarrulla.baseandroid.fragments.listaUbicacionesFragment;
import com.lamarrulla.baseandroid.implement.Acceso;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.interfaces.LatLngInterpolator;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.models.Dispositivo.DispositivoUsuario;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.services.ReadService;
import com.lamarrulla.baseandroid.utils.Constants;
import com.lamarrulla.baseandroid.utils.FirebaseAPI;
import com.lamarrulla.baseandroid.utils.MarkerAnimation;
import com.lamarrulla.baseandroid.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AltaDispositivoFragment.OnFragmentAltaDispositivoInteractionListener,
        OnMapReadyCallback, SearchView.OnQueryTextListener{

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

    private LinearLayout bottomSheet;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    /*private RecyclerView recyclerViewUbicaciones;
    private RecyclerView.Adapter mAdapterUbicaciones;
    private RecyclerView.LayoutManager layoutManagerUbicaciones;*/

    private TextView txtDispositivos;
    private ImageView imgDown;
    private SearchView srchViewDispositivos;

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
    //List<Dispositivo.DispositivosMarks> listDispositivosMarks;

    MarkerAnimation markerAnimation = new MarkerAnimation();

    FloatingActionButton fab;

    List<DispositivoUsuario> dispUsuList;

    FirebaseAPI firebaseAPI = new FirebaseAPI();

    /*SearchView searchMap;*/
    /*SearchView searchPartidaMap;*/

    Location MyLocation;

    int AUTOCOMPLETE_REQUEST_CODE = 1;

    BottomSheetBehavior bsb;

    /*Geocoder geocoder;*/

    /*listaUbicacionesFragment.OnListFragmentInteractionListener listFragmentInteractionListener;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        PlacesClient placesClient = Places.createClient(this);

        /*// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountry("MX");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                utils.newMark(gmap, place.getLatLng(), place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });*/

        Log.d(TAG, "OnCreate");

        /*searchMap = findViewById(R.id.searchMap);*/
        /*searchPartidaMap = findViewById(R.id.searchPartidaMap);*/

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        CambiosEnToolBar();

        //mProgressView = findViewById(R.id.main_progress);
        //mPrincipalSV = findViewById(R.id.svPrincipal);

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        intentReadService = new Intent(getApplicationContext(), ReadService.class);

        /*  inicia localizacion */
        intentTrackerServices = new Intent(MainActivity.this, TrackerActivity.class);
        /*  inicia localizacion */


        fab = (FloatingActionButton) findViewById(R.id.fab);
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

        configuraBottomSheets();

        /*/// busqueda de ubicaciones
        geocoder = new Geocoder(context, Locale.getDefault());
        *//*recyclerViewUbicaciones = (RecyclerView) findViewById(R.id.my_recycler_ubicaciones);*//*

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewUbicaciones.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerUbicaciones = new LinearLayoutManager(this);
        recyclerViewUbicaciones.setLayoutManager(layoutManagerUbicaciones);
        listFragmentInteractionListener = new listaUbicacionesFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Address item) {
                Toast.makeText(context, "Prueba list iteraction", Toast.LENGTH_SHORT).show();
                LatLng itemLatLong = new LatLng(item.getLatitude(), item.getLongitude());
                *//*gmap.addMarker(new MarkerOptions().position(itemLatLong).title(item.getLocality()));
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(itemLatLong, 18));*//*
                utils.newMark(gmap, itemLatLong, item.getLocality());
            }
        };*/

    }

    private void configuraBottomSheets(){
        /*  Manejo del bootomSehhet*/

        bottomSheet = findViewById(R.id.bottomSheet);

        bsb = BottomSheetBehavior.from(bottomSheet);

        //bsb.setHalfExpandedRatio((float) 0.4); // coloca al 40% la barra

        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                /*if (BottomSheetBehavior.STATE_DRAGGING == i) {
                    fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                }else if*/

                int nuevoEstado = BottomSheetBehavior.STATE_COLLAPSED;

                switch(i) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = BottomSheetBehavior.STATE_COLLAPSED;
                        fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = BottomSheetBehavior.STATE_EXPANDED;
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = BottomSheetBehavior.STATE_HIDDEN;
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = BottomSheetBehavior.STATE_DRAGGING;
                        fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = BottomSheetBehavior.STATE_SETTLING;
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        nuevoEstado = BottomSheetBehavior.STATE_HALF_EXPANDED;
                        fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                        break;
                    default:
                        fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                        nuevoEstado = BottomSheetBehavior.STATE_COLLAPSED;
                        break;
                }

                if(nuevoEstado==BottomSheetBehavior.STATE_EXPANDED){
                    getSupportActionBar().hide();
                    imgDown.setVisibility(View.VISIBLE);
                    srchViewDispositivos.setVisibility(View.GONE);
                    txtDispositivos.setVisibility(View.VISIBLE);
                }
                else if(nuevoEstado==BottomSheetBehavior.STATE_HALF_EXPANDED){
                    srchViewDispositivos.setVisibility(View.VISIBLE);
                    txtDispositivos.setVisibility(View.GONE);
                    getSupportActionBar().show();
                }
                else{
                    getSupportActionBar().show();
                    imgDown.setVisibility(View.GONE);
                    srchViewDispositivos.setVisibility(View.GONE);
                    txtDispositivos.setVisibility(View.VISIBLE);
                }

                Log.i("BottomSheets", "Nuevo estado: " + nuevoEstado + " : " + i);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                Log.i("BottomSheets", "Offset: " + v);
                if(v>=0){
                    fab.animate().scaleX(1 - v).scaleY(1 - v).setDuration(0).start();
                    fab.animate().translationX(0).translationY(-fab.getHeight()*v*2).setInterpolator(new LinearInterpolator()).start();
                }
            }
        });

        txtDispositivos = findViewById(R.id.txtDispositivos);
        imgDown = findViewById(R.id.imgDown);
        srchViewDispositivos = findViewById(R.id.srchViewDispositivos);
        srchViewDispositivos.setOnQueryTextListener(this);

        txtDispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bsb.getState()==BottomSheetBehavior.STATE_HALF_EXPANDED){
                    bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }else{
                    bsb.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    srchViewDispositivos.setFocusable(true);
                    srchViewDispositivos.setIconified(false);
                    srchViewDispositivos.requestFocusFromTouch();
                }
            }
        });

        imgDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        /*  termina Manejo del bootomSehhet*/
    }

    private void cargaDispositivosList(){
        /*  Inicia menejo del recycler view */

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        /*List<Dispositivo.User> listUser = new ArrayList<>();
        listUser.add(new Dispositivo.User("jose david rincon","angelnmara@hotmail","","55555555"));
        listUser.add(new Dispositivo.User("andres rincon","arincon@bdda.co","","654654654"));
        listUser.add(new Dispositivo.User("angel rincon","kjadhfkljahd@bdda.co","","654654654"));

        List<DummyContent.DummyItem> items = new ArrayList<>();
        items.add(new DummyContent.DummyItem("1", "dave", ""));
        items.add(new DummyContent.DummyItem("2", "andres", ""));
        items.add(new DummyContent.DummyItem("3", "angel", ""));
        items.add(new DummyContent.DummyItem("4", "byron", ""));*/
        UsersFragment.OnListFragmentInteractionListener listener = new UsersFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(DispositivoUsuario item) {
                Log.d(TAG, "interaccion");
            }
        };
        
        UsersFragment.OnRouteInteractionListener mListenerRoute = new UsersFragment.OnRouteInteractionListener() {
            @Override
            public void onRouteInteractionListener(String dispositivo) {
                //Toast.makeText(context, "Click en ruta", Toast.LENGTH_SHORT).show();
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("MX")
                        .setHint("Origen")
                        .build(context);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

                /*searchMap.setVisibility(View.VISIBLE);
                searchMap.setFocusable(true);
                searchMap.setIconified(false);
                searchMap.requestFocusFromTouch();
                searchMap.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        //Toast.makeText(context, "botonCerrar", Toast.LENGTH_SHORT).show();
                        List<Address> addressList = new ArrayList<>();
                        cargaListaUbicaciones(addressList);
                        return false;
                    }
                });
                searchMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        Toast.makeText(context, "TextSubmit" + s, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        try {
                            List<Address> addressList = new ArrayList<>();
                            if(s.length()>4){
                                addressList = geocoder.getFromLocationName(s, 5, MyLocation.getLatitude(), MyLocation.getLongitude(), MyLocation.getLatitude(), MyLocation.getLongitude());
                                //addressList = geocoder.getFromLocationName(s, 5);
                            }
                            cargaListaUbicaciones(addressList);
                            //Toast.makeText(context, addressList.toString(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });

                searchPartidaMap.setVisibility(View.VISIBLE);
                searchPartidaMap.setFocusable(true);
                searchPartidaMap.setIconified(false);
                searchPartidaMap.requestFocusFromTouch();
                searchPartidaMap.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        //Toast.makeText(context, "botonCerrar", Toast.LENGTH_SHORT).show();
                        List<Address> addressList = new ArrayList<>();
                        cargaListaUbicaciones(addressList);
                        return false;
                    }
                });
                searchPartidaMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        Toast.makeText(context, "TextSubmit" + s, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        try {
                            List<Address> addressList = new ArrayList<>();
                            if(s.length()>4){
                                addressList = geocoder.getFromLocationName(s, 5, MyLocation.getLatitude(), MyLocation.getLongitude(), MyLocation.getLatitude(), MyLocation.getLongitude());
                                //addressList = geocoder.getFromLocationName(s, 5);
                            }
                            cargaListaUbicaciones(addressList);
                            //Toast.makeText(context, addressList.toString(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });*/
            }
        };
        
        mAdapter = new MyUsersRecyclerViewAdapter(dispUsuList, listener, mListenerRoute, context);
        recyclerView.setAdapter(mAdapter);

        /*  termina Inicia menejo del recycler view */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                utils.newMark(gmap, place.getLatLng(), place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /*public void cargaListaUbicaciones(List<Address> listAddres){
        mAdapterUbicaciones = new MylistaUbicacionesRecyclerViewAdapter(listAddres, listFragmentInteractionListener);
        recyclerViewUbicaciones.swapAdapter(mAdapterUbicaciones, false);
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        Log.d(TAG, "onPrepareOptionMenu" + jsaDispositivos.length());
        Log.d(TAG, jsaDispositivos.toString());
        menu.clear();
        /*for(int i = 0; i<jsaDispositivos.length();i++){
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
        }*/
        //menu.add(0,1, Menu.CATEGORY_ALTERNATIVE, "primero").setChecked(true);
        //menu.add(0,2, Menu.CATEGORY_ALTERNATIVE, "primero2");
        //menu.setGroupCheckable(0, true, false);

        if(dispUsuList!=null){
            int i = 0;
            for (DispositivoUsuario du :dispUsuList
            ) {
                if(du.activo){
                    menu.add(0, i, Menu.FLAG_PERFORM_NO_CLOSE, du.usuario).setChecked(du.seleccionado);
                }
                i++;
            }
        }

        menu.setGroupCheckable(0, true, false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.d(TAG, s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        return false;
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
                            // Got last known MyLocation. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle MyLocation object
                                MyLocation = location;
                                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                            }
                        }
                    });
        }
    }

    public void getListaDispositivos(final String dispositivo, final String usuario) {
        //listDispositivosMarks = new ArrayList<>();
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
                                //Dispositivo.DispositivosMarks dispositivosMarks = new Dispositivo.DispositivosMarks();
                                for (DispositivoUsuario du :dispUsuList
                                     ) {
                                    if(du.dispositivo.equals(dispositivo)){
                                        du.marker = marker;
                                    }
                                }

                                /*dispositivosMarks.dispositivo = dispositivo;
                                dispositivosMarks.marker = marker;
                                dispositivosMarks.dispositivoSeleccionado = false;
                                listDispositivosMarks.add(dispositivosMarks);*/
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
                        /*if(listDispositivosMarks!= null){
                            for (Dispositivo.DispositivosMarks dispositivoMarks: listDispositivosMarks
                            ) {
                                if(dispositivoMarks.dispositivo.equals(dispositivo)){
                                    //dispositivoMarks.marker.setPosition(latLng);
                                    LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
                                    markerAnimation.animateMarkerToGB(dispositivoMarks.marker, latLng, latLngInterpolator, 5000);
                                    dispositivoMarks.marker.setRotation(bearing);
                                    if(dispositivoMarks.dispositivoSeleccionado){
                                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                                        // Zoom in, animating the camera.
                                        gmap.animateCamera(CameraUpdateFactory.zoomIn());
                                        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                        gmap.animateCamera(CameraUpdateFactory.zoomTo(18), 5000, null);
                                    }
                                }
                            }
                        }*/
                        if(dispUsuList!=null){
                            for (DispositivoUsuario du: dispUsuList
                                 ) {
                                if(du.dispositivo.equals(dispositivo)){
                                    LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
                                    markerAnimation.animateMarkerToGB(du.marker, latLng, latLngInterpolator, 5000);
                                    du.marker.setRotation(bearing);
                                    if(du.seleccionado){
                                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                                        gmap.animateCamera(CameraUpdateFactory.zoomIn());
                                        gmap.animateCamera(CameraUpdateFactory.zoomTo(18), 5000, null);
                                    }
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
        /*gmap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Toast.makeText(context, "Oncameramove", Toast.LENGTH_SHORT).show();
                //limpia dispositivo seleccionado
                for (Dispositivo.DispositivosMarks dm : listDispositivosMarks
                ) {
                    if(listDispositivosMarks!=null){
                        dm.dispositivoSeleccionado = false;
                    }
                }
            }
        });*/
        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Toast.makeText(context, "click map", Toast.LENGTH_SHORT).show();
                /*if(listDispositivosMarks!=null){
                    for (Dispositivo.DispositivosMarks dm : listDispositivosMarks
                    ) {
                        if(listDispositivosMarks!=null){
                            dm.dispositivoSeleccionado = false;
                        }
                    }
                }*/
                if(dispUsuList!=null){
                    for (DispositivoUsuario du: dispUsuList
                         ) {
                        du.seleccionado = false;
                    }
                }
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
                /*gmap.animateCamera(CameraUpdateFactory.zoomIn());
                gmap.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);*/
                /*for (Dispositivo.DispositivosMarks dm : listDispositivosMarks
                        ) {
                    if(dm.marker.getId().equals(marker.getId())){
                        dm.dispositivoSeleccionado = true;
                    }
                }*/
                if(dispUsuList!=null){
                    for (DispositivoUsuario du: dispUsuList
                         ) {
                        if(du.marker.getId().equals(marker.getId())){
                            du.seleccionado = true;
                        }
                    }
                }
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
            iniciaTodo();
        }
    }

    @SuppressLint("MissingPermission")
    public void iniciaTodo() {
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
                    iniciaTodo();
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
        /*if(dispUsuList!=null){
            int i = 0;
            for (Dispositivo.DispositivoUsuario du :dispUsuList
            ) {
                if(du.activo){
                    menu.add(0, i, Menu.FLAG_PERFORM_NO_CLOSE, du.usuario).setChecked(du.seleccionado);
                }
                i++;
            }
        }

        menu.setGroupCheckable(0, true, false);*/
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*Boolean valor = (item.isChecked()?false:true);

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
        }*/

        for (DispositivoUsuario du :dispUsuList
             ) {
            if(du.usuario.equals(item.toString())){
                if (item.isChecked()) {
                    du.seleccionado = false;
                }
                else {
                    /*for (Dispositivo.DispositivosMarks dm : listDispositivosMarks
                    ) {
                        if(dm.marker.getId().equals(marker.getId())){
                            dm.dispositivoSeleccionado = true;
                        }
                    }*/
                    du.marker.showInfoWindow();
                    du.seleccionado = true;
                }
            }else{
                du.seleccionado = false;
            }
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
        MyLocation = gmap.getMyLocation();
            if(MyLocation !=null){
            LatLng sydney = new LatLng(MyLocation.getLatitude(), MyLocation.getLongitude());
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
        }else{
                getLastLocation();
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopService(intentReadService);

        if(utils.isConnectAvailable(context)){
            if(dispUsuList!=null){
                firebaseAPI.writeNewObject(getString(R.string.dispositivos), dispUsuList);
            }
        }else{
            Toast.makeText(context, getString(R.string.noConexionInternet), Toast.LENGTH_SHORT).show();
        }

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
        /*if(listDispositivosMarks != null){
            for (Dispositivo.DispositivosMarks dm: listDispositivosMarks
            ) {
                dm.marker.remove();
            }
        }*/
        if(dispUsuList!=null){
            for (DispositivoUsuario du: dispUsuList
                 ) {
                du.marker.remove();
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

                                TypeToken<List<DispositivoUsuario>> token = new TypeToken<List<Dispositivo.DispositivoUsuario>>() {};
                                dispUsuList = gso.fromJson(s1, token.getType());
                                cargaDispositivosList();

                                jsaDispositivos = new JSONArray(s1);
                                /*JSONArray jsaTemp = new JSONArray(s1);
                                compareJSA(jsaDispositivos, jsaTemp);*/
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
        /*for(int i = 0; i<jsaDispositivos.length();i++){
            JSONObject jso = jsaDispositivos.getJSONObject(i);
            if(!jso.has("valor")){
                jso.put("valor", jso.getBoolean("activo"));
            }
            if(jso.getBoolean("valor")){
                getListaDispositivos(jso.getString("dispositivo"), jso.getString("usuario"));
            }
        }*/
        for (DispositivoUsuario du:dispUsuList
             ) {
            getListaDispositivos(du.dispositivo, du.usuario);
        }
        ejecutaIntent(jsaDispositivos.toString());
        invalidateOptionsMenu();
    }
}