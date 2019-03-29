package com.lamarrulla.baseandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.lamarrulla.baseandroid.R;

import java.lang.reflect.Field;

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
    public int getResourceDrawforName(String drawableName){
        int drawableId = 0;
        try{
            Class res = R.drawable.class;
            Field field = res.getField(drawableName);
            drawableId = field.getInt(null);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return drawableId;
    }
}
