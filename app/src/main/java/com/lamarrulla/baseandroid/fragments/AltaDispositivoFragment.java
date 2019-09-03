package com.lamarrulla.baseandroid.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
/*import android.support.v4.app.Fragment;*/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.utils.FirebaseAPI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentAltaDispositivoInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AltaDispositivoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AltaDispositivoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnAgregar;
    private TextView txtMac;
    private TextView txtUsuario;

    private OnFragmentAltaDispositivoInteractionListener mListener;

    private DatabaseReference mDatabase;

    FirebaseAPI firebaseAPI = new FirebaseAPI();

    public AltaDispositivoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AltaDispositivoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AltaDispositivoFragment newInstance(String param1, String param2) {
        AltaDispositivoFragment fragment = new AltaDispositivoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alta_dispositivo, container, false);
        txtMac = view.findViewById(R.id.txtMacAddres);
        txtUsuario = view.findViewById(R.id.txtUsuario);
        btnAgregar = view.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), txtMac.getText(), Toast.LENGTH_LONG).show();
                List listDispositivoUsuario = new ArrayList();
                Dispositivo.Fecha date = new Dispositivo.Fecha();
                /*Dispositivo.DispositivoUsuario dispositivoUSuario = new Dispositivo.DispositivoUsuario(txtMac.getText().toString(),
                        true,
                        date,
                        null);*/
                listDispositivoUsuario.add(new Dispositivo.DispositivoUsuario(txtMac.getText().toString(),
                        txtUsuario.getText().toString(),
                        true,
                        date,
                        null,
                        false,
                        false,
                        null));
                /*listDispositivoUsuario.add(new Dispositivo.DispositivoUsuario(txtMac.getText().toString(),
                        false,
                        date,
                        date));*/
                firebaseAPI.writeNewObject("dispositivos", listDispositivoUsuario);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentAltaDispositivoInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAltaDispositivoInteractionListener) {
            mListener = (OnFragmentAltaDispositivoInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentAltaDispositivoInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentAltaDispositivoInteractionListener {
        // TODO: Update argument type and name
        void onFragmentAltaDispositivoInteraction(Uri uri);
    }
}
