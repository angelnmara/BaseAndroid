package com.lamarrulla.baseandroid.implement;

import android.content.Context;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.utils.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Acceso {

    API api = new API();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Boolean getAccesoCorrecto() {
        return accesoCorrecto;
    }

    public void setAccesoCorrecto(Boolean accesoCorrecto) {
        this.accesoCorrecto = accesoCorrecto;
    }

    private String username;
    private String password;
    private String token;
    private String salt;
    private Context context;
    private Boolean accesoCorrecto;

    public void validaUsuario() throws IOException, URISyntaxException, JSONException {
        accesoCorrecto = false;
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
                setToken(jso.getString(context.getString(R.string.Token)));
                setSalt(jso.getString(context.getString(R.string.Salt)));
                accesoCorrecto = true;
            }else{
                accesoCorrecto = false;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            accesoCorrecto = false;
        }
    }

}
