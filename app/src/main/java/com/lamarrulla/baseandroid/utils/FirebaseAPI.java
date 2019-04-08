package com.lamarrulla.baseandroid.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lamarrulla.baseandroid.models.Dispositivo;

public class FirebaseAPI {
    private static final String TAG = "FirebaseAPI";
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    public void writeNewUser(){
        try{
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            Dispositivo.User user = new Dispositivo.User(mFirebaseAuth.getUid(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getEmail(),
                    (firebaseUser.getPhotoUrl()==null)?"":firebaseUser.getPhotoUrl().toString(),
                    firebaseUser.getPhoneNumber());
            mDatabase.child("users").child(mFirebaseAuth.getUid()).setValue(user);
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }
}
