package com.lamarrulla.baseandroid.implement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lamarrulla.baseandroid.MainActivity;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.utils.API;
import com.lamarrulla.baseandroid.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Acceso implements IAcceso {

    Utils utils = new Utils();

    private FirebaseAuth mAuth;

    private final String TAG = Acceso.class.getSimpleName();

    API api = new API();

    public void setUsername(String user) {
        this.username = user;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public void setContext(Context cont) {
        this.context = cont;
    }

    public Boolean getEsCorrecto() {
        return esCorrecto;
    }

    public JSONObject getJso() {
        return jso;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    private String username;
    private String password;
    private Context context;
    private Boolean esCorrecto = false;
    private JSONObject jso;
    private String tabla;

    public void
    ejecutaSelect() throws IOException, URISyntaxException, JSONException {
        try{
            api.setContext(context);
            api.setUrl(context.getString(R.string.Server) + String.format(context.getString(R.string.APISelectAll), "tbmodulo"));
            api.setTipoPeticion(1);
            api.setSecure(true);
            api.EjecutaAPI();
            if(api.isResponseOK()){
                System.out.println(api.getSalida());
                jso = new JSONObject(api.getSalida());
                esCorrecto = true;
            }else{
                System.out.println(api.getSalida());
                esCorrecto = false;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public void autenticaUsuarioServer(){
        try{
            api.setContext(context);
            api.setUrl(context.getString(R.string.Server) + context.getString(R.string.Authentitacion));
            api.setTipoPeticion(2);
            ArrayList<Login.Parametro> parametroList = new ArrayList<>();
            parametroList.add(new Login.Parametro(context.getString(R.string.Username), username));
            parametroList.add(new Login.Parametro(context.getString(R.string.Password), password));
            api.setParametroList(parametroList);
            api.EjecutaAPI();

            if(api.isResponseOK()){
                System.out.println(api.getSalida());
                JSONObject jso = new JSONObject(api.getSalida());
                //token = jso.getString(context.getString(R.string.Token));
                //salt = jso.getString(context.getString(R.string.Salt));
                //username = jso.getString(context.getString(R.string.Username));
                utils.guardaShared((Activity) context, R.string.Token, jso.getString(context.getString(R.string.Token)));
                utils.guardaShared((Activity) context, R.string.Salt, jso.getString(context.getString(R.string.Salt)));
                utils.guardaShared((Activity) context, R.string.Username, jso.getString(context.getString(R.string.Username)));
                utils.guardaShared((Activity) context, R.string.TipoAcceso, "1");
                esCorrecto = true;
            }else{
                esCorrecto = false;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            esCorrecto = false;
        }
    }

    @Override
    public void autenticaUsuarioFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            utils.guardaShared((Activity) context, R.string.Token, user.toString());
                            utils.guardaShared((Activity) context, R.string.TipoAcceso, "2");
                            utils.OpenMain(context);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, context.getString(R.string.AutenticacionInvalida),
                                    Toast.LENGTH_SHORT).show();
                            showProgress();
                        }
                    }
                });
    }

    @Override
    public void altaUsarioServer() {
        Toast.makeText(context, "alta usuario server", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "altaUsarioServer");
    }

    @Override
    public void altaUsuarioFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //utils.guardaShared((Activity) context, R.string.Token, user.toString());
                            utils.guardaShared((Activity) context, R.string.TipoAcceso, "2");
                            //utils.OpenMain(context);
                            utils.OpenAltaUsuario(context);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            showProgress();
                        }
                    }
                });
    }
    public void showProgress(){
        View mLoginFormView = ((Activity)context).findViewById(R.id.login_form);
        EditText passw = mLoginFormView.findViewById(R.id.password);
        passw.setText("");
        View mProgressView = ((Activity)context).findViewById(R.id.login_progress);
        utils.showProgress(mLoginFormView, mProgressView, context);
    }
}
