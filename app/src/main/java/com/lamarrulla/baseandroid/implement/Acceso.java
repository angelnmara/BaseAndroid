package com.lamarrulla.baseandroid.implement;

import android.content.Context;
import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.interfaces.IAcceso;
import com.lamarrulla.baseandroid.models.Login;
import com.lamarrulla.baseandroid.utils.API;
import org.json.JSONObject;
import java.util.ArrayList;

public class Acceso implements IAcceso {

    API api = new API();

    /*public String getUsername() {
        return username;
    }*/

    public void setUsername(String user) {
        this.username = user;
    }

    /*public String getPassword() {
        return password;
    }*/

    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getToken() {
        return token;
    }

    /*public void setToken(String token) {
        this.token = token;
    }*/

    public String getSalt() {
        return salt;
    }

    /*public void setSalt(String salt) {
        this.salt = salt;
    }*/

    /*public Context getContext() {
        return context;
    }*/

    public void setContext(Context cont) {
        this.context = cont;
    }

    public Boolean getAccesoCorrecto() {
        return accesoCorrecto;
    }

    /*public void setAccesoCorrecto(Boolean accesoCorrecto) {
        this.accesoCorrecto = accesoCorrecto;
    }*/

    private String username;
    private String password;
    private String token;
    private String salt;
    private Context context;
    private Boolean accesoCorrecto;

    public boolean validaUsuario(){
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
                token = jso.getString(context.getString(R.string.Token));
                salt = jso.getString(context.getString(R.string.Salt));
                accesoCorrecto = true;
            }else{
                accesoCorrecto = false;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            accesoCorrecto = false;
        }
        return accesoCorrecto;
    }
}
