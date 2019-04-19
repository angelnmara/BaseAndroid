package com.lamarrulla.baseandroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.lamarrulla.baseandroid.utils.MaskWatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AltaDeviceActivity extends AppCompatActivity {

    private static final String TAG = "DispositivosFragment";
    private static final String TAGADSF = "AltaDispositivoScanFragment";
    private TextView mTextMessage;
    private int mColumnCount = 1;
    FirebaseAPI firebaseAPI = new FirebaseAPI();
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    List<Dispositivo.DispositivoUsuario> listDispositivoUsuario;
    RecyclerView.Adapter adapter;
    Context context = AltaDeviceActivity.this;
    CoordinatorLayout lnlAltaMAC;
    LinearLayout lnlAltaScan;

    public interface OnItemClickListener{
        void onItemClick(Dispositivo.DispositivoUsuario item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    lnlAltaScan.setVisibility(View.GONE);
                    lnlAltaMAC.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    lnlAltaMAC.setVisibility(View.GONE);
                    lnlAltaScan.setVisibility(View.VISIBLE);
                    return true;
                /*case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;*/
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_device);
        lnlAltaScan = (LinearLayout) findViewById(R.id.lnlAltaScan);
        lnlAltaMAC = (CoordinatorLayout) findViewById(R.id.lnlAltaMAC);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.altaDispositvo);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
    }

    public void createDialog(final Dispositivo.DispositivoUsuario item, final int opcion){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mViewAgregar = getLayoutInflater().inflate(R.layout.fragment_alta_dispositivo, null);
        mBuilder.setView(mViewAgregar);
        final AlertDialog dialog = mBuilder.create();
        final EditText txtMacAddres = mViewAgregar.findViewById(R.id.txtMacAddres);
        if(item!=null){
            txtMacAddres.setText(item.dispositivo);
        }
        txtMacAddres.addTextChangedListener(new MaskWatcher("##:##:##:##:##:##"));
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
                        true,
                        date,
                        null
                ));
                adapter.notifyItemInserted(listDispositivoUsuario.size() - 1);
                adapter.notifyDataSetChanged();
                dialog.hide();
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