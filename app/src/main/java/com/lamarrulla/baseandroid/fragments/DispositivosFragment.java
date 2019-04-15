package com.lamarrulla.baseandroid.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.utils.FirebaseAPI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentDispositivosInteractionListener}
 * interface.
 */
public class DispositivosFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "DispositivosFragment";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    FirebaseAPI firebaseAPI = new FirebaseAPI();
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    List<Dispositivo.DispositivoUsuario> listDispositivoUsuario;
    RecyclerView.Adapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DispositivosFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DispositivosFragment newInstance(int columnCount) {
        DispositivosFragment fragment = new DispositivosFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dispositivos_list, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
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
                    adapter = new MyDispositivosRecyclerViewAdapter(listDispositivoUsuario);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Button btnAgregar = view.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mViewAgregar = getLayoutInflater().inflate(R.layout.fragment_alta_dispositivo, null);
                mBuilder.setView(mViewAgregar);
                final AlertDialog dialog = mBuilder.create();
                final EditText txtMacAddres = mViewAgregar.findViewById(R.id.txtMacAddres);
                Button btnAgregar = mViewAgregar.findViewById(R.id.btnAgregar);
                btnAgregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date date = new Date();
                        listDispositivoUsuario.add(new Dispositivo.DispositivoUsuario(
                                txtMacAddres.getText().toString(),
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
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        firebaseAPI.writeNewObject(getString(R.string.dispositivos), listDispositivoUsuario);
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseAPI.writeNewObject(getString(R.string.dispositivos), listDispositivoUsuario);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
