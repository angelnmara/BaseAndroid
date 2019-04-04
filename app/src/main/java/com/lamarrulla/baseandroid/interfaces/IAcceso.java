package com.lamarrulla.baseandroid.interfaces;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IAcceso {
    void setContext(Context con);
    void setUsername(String user);
    void setPassword(String pass);
    void setTabla(String tabla);
    Boolean getEsCorrecto();
    JSONObject getJso();
    void autenticaUsuarioServer() throws IOException, URISyntaxException, JSONException;
    void autenticaUsuarioFirebase();
    void altaUsarioServer();
    void altaUsuarioFirebase();
    void ejecutaSelect() throws IOException, URISyntaxException, JSONException;
}
