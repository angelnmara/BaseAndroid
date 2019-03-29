package com.lamarrulla.baseandroid.implement;

import android.content.Context;
import android.content.Intent;

import com.lamarrulla.baseandroid.LoginActivity;
import com.lamarrulla.baseandroid.MainActivity;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.utils.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Acceso implements IAcceso {

    API api = new API();

    public void setUsername(String user) {
        this.username = user;
    }

    public String getUsername(){
        return username;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getToken() {
        return token;
    }

    public String getSalt() {
        return salt;
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
    private String token;
    private String salt;
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

    public void autenticaUsuario(){
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
                token = jso.getString(context.getString(R.string.Token));
                salt = jso.getString(context.getString(R.string.Salt));
                username = jso.getString(context.getString(R.string.Username));
                esCorrecto = true;
            }else{
                esCorrecto = false;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            esCorrecto = false;
        }
    }
}
