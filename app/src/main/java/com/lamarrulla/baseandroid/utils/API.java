package com.lamarrulla.baseandroid.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

    public void EjecutaAPI() throws IOException, URISyntaxException {
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
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", "daver2"));
            nameValuePairs.add(new BasicNameValuePair("password", "12345678"));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(request);
        }

        in = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
        // NEW CODE
        salida = in.readLine();
        System.out.println(salida);
    }
}