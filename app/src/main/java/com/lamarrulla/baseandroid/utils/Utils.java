package com.lamarrulla.baseandroid.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;

import com.lamarrulla.baseandroid.MainActivity;
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View ShowView, final View HiddenView, Context context) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            ShowView.setVisibility(show ? View.GONE : View.VISIBLE);
            ShowView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ShowView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            HiddenView.setVisibility(show ? View.VISIBLE : View.GONE);
            HiddenView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    HiddenView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            HiddenView.setVisibility(show ? View.VISIBLE : View.GONE);
            ShowView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public void OpenMain(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
