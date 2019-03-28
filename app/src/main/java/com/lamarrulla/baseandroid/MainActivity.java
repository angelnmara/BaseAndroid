package com.lamarrulla.baseandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.lamarrulla.baseandroid.implement.Acceso;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.models.Login;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    IAcceso iAcceso = new Acceso();
    Context context = this;
    private View mProgressView;
    private View mPrincipalSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressView = findViewById(R.id.login_progress);
        mPrincipalSV = findViewById(R.id.svPrincipal);

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        /*  select menu */
        new getMenu(context, "tbmodulo");
        if(iAcceso.getEsCorrecto()){
            agregaMenu(navigationView, null);
        }else{
            Toast.makeText(context, "No se puede obtener el menu", Toast.LENGTH_SHORT).show();
        }
        /*  select menu */
        navigationView.setNavigationItemSelectedListener(this);
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
                showProgress(true);
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
                System.out.println("post succes");
            } else {
                System.out.println("post fail");
                mPrincipalSV.requestFocus();
                showProgress(false);
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
                    int resource = getResources().getIdentifier(m.nombreMenu, "drawable", getPackageName());
                    menu.add(1, m.ordenMenu, m.ordenMenu, m.nombreMenu).setIcon(resource);
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
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
