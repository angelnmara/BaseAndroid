package com.lamarrulla.baseandroid.interfaces;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IAcceso {
    void setContext(Context con);
    void setUsername(String user);
    void setPassword(String pass);
    String getToken();
    String getSalt();
    Boolean getAccesoCorrecto();
    boolean validaUsuario() throws IOException, URISyntaxException, JSONException;
}
