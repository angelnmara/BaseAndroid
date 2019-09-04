package com.lamarrulla.baseandroid.adapters;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.fragments.UsersFragment.OnListFragmentInteractionListener;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent.DummyItem;
import com.lamarrulla.baseandroid.models.Dispositivo.DispositivoUsuario;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUsersRecyclerViewAdapter extends RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private final List<DispositivoUsuario> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    public static final String TAG = MyUsersRecyclerViewAdapter.class.getSimpleName();
    private final boolean favoritos = false;

    public MyUsersRecyclerViewAdapter(List<DispositivoUsuario> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).dispositivo);
        holder.mContentView.setText(mValues.get(position).usuario);
        holder.mImgFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mValues.get(position).favorito){
                    ImageViewCompat.setImageTintList((ImageView) v, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent)));
                }else{
                    ImageViewCompat.setImageTintList((ImageView) v, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorOnSecondary)));
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgFavoritos:
                Log.d(TAG, "mensajen favoritos");
                if(!favoritos){
                    ImageViewCompat.setImageTintList((ImageView) view, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent)));
                }else{
                    ImageViewCompat.setImageTintList((ImageView) view, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorOnSecondary)));
                }
                break;
                default:
                    Log.d(TAG, "default click");
                    break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImgFavoritos;
        public DispositivoUsuario mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImgFavoritos = (ImageView)view.findViewById(R.id.imgFavoritos);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
