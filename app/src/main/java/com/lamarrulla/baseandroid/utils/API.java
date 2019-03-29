package com.lamarrulla.baseandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.models.Login;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class API {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public int getTipoPeticion() {
        return tipoPeticion;
    }

    public void setTipoPeticion(int tipoPeticion) {
        this.tipoPeticion = tipoPeticion;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isResponseOK() {
        return responseOK;
    }

    public void setResponseOK(boolean responseOK) {
        this.responseOK = responseOK;
    }

    public List<Login.Parametro> getParametroList() {
        return parametroList;
    }

    public void setParametroList(List<Login.Parametro> parametroList) {
        this.parametroList = parametroList;
    }

    private String url;
    private String salida;
    /*
        tipoPeticion
        1.- Get
        2.- Post
        3.- Put
        4.- Delete
    */
    private int tipoPeticion;
    /*
        True.- Secure
        False.- Unsecure
    */
    private boolean secure;
    private Context context;
    private boolean responseOK;
    private List<Login.Parametro> parametroList;
    private String token;
    private String salt;
    private String username;

    private void getCredenciales(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        token = sharedPreferences.getString(context.getString(R.string.Token), "");
        salt = sharedPreferences.getString(context.getString(R.string.Salt), "");
        username = sharedPreferences.getString(context.getString(R.string.Username), "");
    }

    public void EjecutaAPI() throws IOException, URISyntaxException {
        try{
            HttpClient httpclient = new DefaultHttpClient();
            BufferedReader in = null;
            HttpResponse response = null;
            int code = 0;
            String mensajeError ="";

            if(tipoPeticion == 1){
                URL website = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)website.openConnection();
                if(secure){
                    getCredenciales();
                    conn.setRequestProperty("Authorization", String.format(context.getString(R.string.BarerToken), token));
                    conn.setRequestProperty(context.getString(R.string.Secret), salt);
                    conn.setRequestProperty(context.getString(R.string.Username), username);
                    conn.setRequestProperty("Content-Type","application/json");
                    conn.setRequestMethod("GET");
                    code = conn.getResponseCode();
                    mensajeError = conn.getResponseMessage();
                }
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //HttpGet request = new HttpGet();
                //request.setURI(website);
                //response = httpclient.execute(request);
            }else if(tipoPeticion == 2){
                HttpPost request = new HttpPost(url);
                //request.setURI(website);
                if(parametroList.size()>0){
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(parametroList.size());
                    for (Login.Parametro param : parametroList
                         ) {
                        nameValuePairs.add(new BasicNameValuePair(param.nombreParametro, param.valorParametro));
                    }
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                }
                response = httpclient.execute(request);
                in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                StatusLine sl = response.getStatusLine();
                code = sl.getStatusCode();
                mensajeError = sl.getReasonPhrase();
            }

            if(code != 200){
                salida =  String.format(context.getString(R.string.StatusLine), mensajeError, code);
                responseOK = false;
            }
            else{

                // NEW CODE
                salida = in.readLine();
                System.out.println(salida);
                responseOK = true;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            responseOK = false;
        }
    }
}