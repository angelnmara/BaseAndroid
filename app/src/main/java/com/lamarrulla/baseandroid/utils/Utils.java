package com.lamarrulla.baseandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.lamarrulla.baseandroid.R;

public class Utils {
    public void guardaShared(Activity activity, int variable, String valor){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(variable), valor);
        editor.commit();
    }
    public void removeShared(Activity activity, int variable){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().remove(activity.getString(variable)).commit();
    }
}
