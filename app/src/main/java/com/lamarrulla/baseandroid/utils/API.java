package com.lamarrulla.baseandroid.utils;

import android.content.Context;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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

    public void EjecutaAPI() throws IOException, URISyntaxException {
        try{
            HttpClient httpclient = new DefaultHttpClient();
            BufferedReader in = null;
            HttpResponse response = null;

            URI website = new URI(url);
            if(tipoPeticion == 1){
                HttpGet request = new HttpGet();
                request.setURI(website);
                response = httpclient.execute(request);
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
            }
            StatusLine sl = response.getStatusLine(); 
            if(sl.getStatusCode()!=200){
                salida =  String.format(context.getString(R.string.StatusLine), sl.getReasonPhrase(), sl.getStatusCode());
                responseOK = false;
            }
            else{
                in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                // NEW CODE
                salida = in.readLine();
                System.out.println(salida);
                responseOK = true;
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}