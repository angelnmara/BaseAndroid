package com.lamarrulla.baseandroid.adapters;

/*import android.support.v7.widget.RecyclerView;*/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/*import android.widget.AdapterView;*/
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.activities.AltaDeviceActivity;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent.DummyItem;
import com.lamarrulla.baseandroid.models.Dispositivo;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentDispositivosInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDispositivosRecyclerViewAdapter extends RecyclerView.Adapter<MyDispositivosRecyclerViewAdapter.ViewHolder>{

    private final List<Dispositivo.DispositivoUsuario> mValues;
    private final AltaDeviceActivity.OnItemClickListener mListener;

    public MyDispositivosRecyclerViewAdapter(List<Dispositivo.DispositivoUsuario> listDispositivoUsuario, AltaDeviceActivity.OnItemClickListener onItemClickListener) {
        mValues = listDispositivoUsuario;
        mListener = (AltaDeviceActivity.OnItemClickListener) onItemClickListener;
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
        final String usuario = mValues.get(position).usuario;
        holder.mtxtUsuario.setText(usuario);
        holder.mContentView.setChecked(check);

        holder.mContentView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked!=holder.mItem.activo){
                    holder.mContentView.setChecked(isChecked);
                    holder.mItem.activo = isChecked;
                    mValues.set(position, holder.mItem);
                    notifyItemChanged(position, null);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentDispositivosInteraction(holder.mItem);
                }*/
                mListener.onItemClick(holder.mItem);
            }
        });

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValues.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mValues.size());
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
        public final ImageView mImage;
        public final TextView mtxtUsuario;

        public Dispositivo.DispositivoUsuario mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (Switch) view.findViewById(R.id.content);
            mImage = (ImageView)view.findViewById(R.id.delete);
            mtxtUsuario = view.findViewById(R.id.txtUsuario);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}