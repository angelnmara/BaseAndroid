package com.lamarrulla.baseandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.lamarrulla.baseandroid.fragments.AltaDispositivoFragment;
import com.lamarrulla.baseandroid.fragments.DispositivosFragment;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent;
import com.lamarrulla.baseandroid.implement.Acceso;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AltaDispositivoFragment.OnFragmentAltaDispositivoInteractionListener,
        DispositivosFragment.OnListFragmentDispositivosInteractionListener,
        DispositivosFragment.OnSwitchFragmentListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressView = findViewById(R.id.main_progress);
        mPrincipalSV = findViewById(R.id.svPrincipal);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        TipoAcceso = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.TipoAcceso), ""));

        if(TipoAcceso == 1){
            /*  obtine modulo desde servidor    */
            /*  select menu */
            showProgress(true);
            mAuthTask = new getMenu(context, "tbmodulo");
            mAuthTask.execute((Void)null);
            /*  select menu */
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onFragmentAltaDispositivoInteraction(Uri uri) {
        Log.d(TAG, "interaccion de fragment");
    }

    @Override
    public void onListFragmentDispositivosInteraction(Dispositivo.DispositivoUsuario item) {
        Log.d(TAG, "interaccion de fragment");
    }

    @Override
    public void onSwitchFragmentInteraction(String dispositivo, boolean valor) {
        Log.d(TAG, dispositivo + " " + valor);
        Toast.makeText(this, dispositivo + " " + valor, Toast.LENGTH_LONG).show();
    }

    public class getMenu extends AsyncTask<Void, Void, Boolean>{

        private final Context mContext;
        private final String mTabla;

        getMenu(Context context, String tabla){
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
                try{
                    JSONObject jso = iAcceso.getJso();
                    JSONArray jsa = jso.getJSONArray("tbmodulo");
                    ArrayList<Login.Menu> menuList = new ArrayList<Login.Menu>();
                    for(int i = 0; i<jsa.length(); i++){
                        JSONObject jsoM = jsa.getJSONObject(i);
                        menuList.add(new Login.Menu(jsoM.getString("fcmodulo"), jsoM.getString("fcmoduloimg"), i));
                    }
                    agregaMenu(navigationView, menuList);
                }catch (Exception ex){
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

    public void agregaMenu(NavigationView navigationView, List<Login.Menu> menuList){
        try{
            /* Obten menu */
            Menu menu =navigationView.getMenu();
            /* Limpia menu */
            menu.clear();
            if(!menuList.isEmpty()){
                for (Login.Menu m: menuList
                ){
                    menu.add(1, m.ordenMenu, m.ordenMenu, m.nombreMenu).setIcon(utils.getResourceDrawforName(m.imagenMenu));
                }
            }
        }catch (Exception ex){
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(this, "alta dispositivo", Toast.LENGTH_SHORT).show();
            AltaDispositivoFragment altaDispositivoFragment = new AltaDispositivoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.lnlPrincipalFragment, altaDispositivoFragment, altadispositivofragment)
                    .addToBackStack(altadispositivofragment)
                    .commit();
        } else if (id == R.id.nav_gallery) {
            DispositivosFragment dispositivosFragment = new DispositivosFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.lnlPrincipalFragment, dispositivosFragment, dispositivosfragment)
                    .addToBackStack(dispositivosfragment)
                    .commit();
        /*} else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {*/

        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "share", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_exit) {
            salir();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void salir() {
        switch (TipoAcceso){
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

    private void regresaLogin(){
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
}
