package com.lamarrulla.baseandroid.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.fragments.DispositivosFragment.OnListFragmentDispositivosInteractionListener;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent.DummyItem;
import com.lamarrulla.baseandroid.models.Dispositivo;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentDispositivosInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDispositivosRecyclerViewAdapter extends RecyclerView.Adapter<MyDispositivosRecyclerViewAdapter.ViewHolder> {

    private final List<Dispositivo.DispositivoUsuario> mValues;
    private final OnListFragmentDispositivosInteractionListener mListener;
    private final DispositivosFragment.OnSwitchFragmentListener mListener2;

    public MyDispositivosRecyclerViewAdapter(List<Dispositivo.DispositivoUsuario> items,
                                             OnListFragmentDispositivosInteractionListener listener,
                                             DispositivosFragment.OnSwitchFragmentListener listener2) {
        mValues = items;
        mListener = listener;
        mListener2 = listener2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dispositivos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        final String dispositivo = mValues.get(position).dispositivo;
        holder.mIdView.setText(dispositivo);
        final boolean check = mValues.get(position).activo;
        holder.mContentView.setChecked(check);
                //.setText(mValues.get(position).activo);

        holder.mContentView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener2.onSwitchFragmentInteraction(dispositivo, isChecked);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentDispositivosInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final Switch mContentView;
        public Dispositivo.DispositivoUsuario mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (Switch) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}