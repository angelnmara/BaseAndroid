package com.lamarrulla.baseandroid.adapters;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Adapter;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.fragments.UsersFragment;
import com.lamarrulla.baseandroid.fragments.UsersFragment.OnListFragmentInteractionListener;
import com.lamarrulla.baseandroid.fragments.dummy.DummyContent.DummyItem;
//import com.lamarrulla.baseandroid.models.Dispositivo;
import com.lamarrulla.baseandroid.models.Dispositivo.DispositivoUsuario;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUsersRecyclerViewAdapter extends RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<DispositivoUsuario> mValues;
    private List<DispositivoUsuario> mValuesFiltrados;
    private final OnListFragmentInteractionListener mListener;
    private final UsersFragment.OnRouteInteractionListener mListenerRoute;
    private final Context mContext;
    public static final String TAG = MyUsersRecyclerViewAdapter.class.getSimpleName();
    /*private final boolean favoritos = false;*/

    public MyUsersRecyclerViewAdapter(List<DispositivoUsuario> items, OnListFragmentInteractionListener listener, UsersFragment.OnRouteInteractionListener mListenerRoute, Context context) {
        mValues = items;
        mValuesFiltrados = items;
        mListener = listener;
        this.mListenerRoute = mListenerRoute;
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
        if(mValues.get(position).favorito){
            ImageViewCompat.setImageTintList(holder.mImgFavoritos, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent)));
        }else{
            ImageViewCompat.setImageTintList(holder.mImgFavoritos, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorOnSecondary)));
        }
        holder.mImgFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mValues.get(position).favorito){
                    ImageViewCompat.setImageTintList((ImageView) v, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent)));
                    mValues.get(position).favorito = false;
                    notifyDataSetChanged();
                }else{
                    ImageViewCompat.setImageTintList((ImageView) v, ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorOnSecondary)));
                    mValues.get(position).favorito = true;
                    notifyDataSetChanged();
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

        holder.mImgRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListenerRoute.onRouteInteractionListener(mValues.get(position).dispositivo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mValuesFiltrados = mValues;
                } else {
                    List<DispositivoUsuario> filteredList = new ArrayList<>();
                    for (DispositivoUsuario row : mValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.usuario.toLowerCase().contains(charString.toLowerCase()) || row.dispositivo.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    mValuesFiltrados = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mValuesFiltrados;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mValuesFiltrados = (ArrayList<DispositivoUsuario>) filterResults.values;
                notifyDataSetChanged();
                /*if (filterResults.count > 0) {
                    notifyDataSetChanged();
                }*/
/*                else {

                }*/
            }
        };
    }

    /*@Override
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
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImgFavoritos;
        public final ImageView mImgRoute;
        public DispositivoUsuario mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImgFavoritos = (ImageView)view.findViewById(R.id.imgFavoritos);
            mImgRoute = view.findViewById(R.id.imgRoute);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
