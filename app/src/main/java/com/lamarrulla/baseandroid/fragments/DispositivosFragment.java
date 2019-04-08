package com.lamarrulla.baseandroid.fragments;

import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent.DummyItem;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.utils.FirebaseAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private OnListFragmentDispositivosInteractionListener mListener;
    FirebaseAPI firebaseAPI = new FirebaseAPI();

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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            final List<Dispositivo.User> listUser = new ArrayList();
            Query query = mDatabase.child("users").child("aYS6q2jjIXNznXlAPVSbhl3E5o42");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                    /*for(DataSnapshot user : dataSnapshot.getChildren()){
                        Log.d(TAG, user.getValue().toString());
                    }*/
                        Dispositivo.User user = dataSnapshot.getValue(Dispositivo.User.class);
                        listUser.add(user);
                    /*try {
                        Gson gso = new Gson();
                        String s1 = gso.toJson(dataSnapshot.getValue());
                        JSONObject jso = new JSONObject(s1);
                        Log.d(TAG, jso.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    }
                    recyclerView.setAdapter(new MyDispositivosRecyclerViewAdapter(DummyContent.ITEMS, mListener));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentDispositivosInteractionListener) {
            mListener = (OnListFragmentDispositivosInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentDispositivosInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnListFragmentDispositivosInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentDispositivosInteraction(DummyItem item);
    }
}
