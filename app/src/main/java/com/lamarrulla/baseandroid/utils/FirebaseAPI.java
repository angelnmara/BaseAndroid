package com.lamarrulla.baseandroid.utils;

/*import android.support.annotation.NonNull;*/
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lamarrulla.baseandroid.models.Dispositivo;

public class FirebaseAPI {
    private static final String TAG = "FirebaseAPI";
    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    public FirebaseAPI(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    public void writeNewUser(){
        try{
            Dispositivo.User user = new Dispositivo.User(
                    (mFirebaseUser.getDisplayName()==null?"":mFirebaseUser.getDisplayName()),
                    mFirebaseUser.getEmail(),
                    (mFirebaseUser.getPhotoUrl()==null)?"":mFirebaseUser.getPhotoUrl().toString(),
                    (mFirebaseUser.getPhoneNumber()==null)?"":mFirebaseUser.getPhoneNumber());
            mDatabase.child("users").child(mFirebaseAuth.getUid()).setValue(user);
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }
    public void writeNewObject(String path, Object object){
        try{
            mDatabase.child(path).child(mFirebaseAuth.getUid()).setValue(object);
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }

    }

    public void deleteObject(final String path, final String value, final String key){
        mDatabase.child(path).child(mFirebaseAuth.getUid());
        Query query = mDatabase.child(path).child(mFirebaseAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot du : dataSnapshot.getChildren()){
                        if(value.contentEquals(du.child(key).getValue().toString())){
                            mDatabase.child(path).child(mFirebaseAuth.getUid()).child(du.getKey()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
