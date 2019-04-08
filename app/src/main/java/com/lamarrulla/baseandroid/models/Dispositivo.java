package com.lamarrulla.baseandroid.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Dispositivo {
    @IgnoreExtraProperties
    public static class User{
        public String uid;
        public String nombre;
        public String email;
        public String urlFoto;
        public String telefono;
        public User(){
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }
        public User(String uid, String nombre, String email, String urlFoto, String telefono){
            this.uid = uid;
            this.nombre = nombre;
            this.email = email;
            this.urlFoto = urlFoto;
            this.telefono = telefono;
        }
    }
    @IgnoreExtraProperties
    public static class DispositivoUSuario{
        public String dispositivo;
        public boolean activo;
        public Date fechaAlta;
        public Date fechaBaja;
        public DispositivoUSuario(){
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }
        public DispositivoUSuario(String dispositivo, boolean activo, Date fechaAlta, Date fechaBaja){
            this.dispositivo = dispositivo;
            this.activo = activo;
            this.fechaAlta = fechaAlta;
            this.fechaBaja = fechaBaja;
        }
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("dispositivo", dispositivo);
            result.put("activo", activo);
            result.put("fechaAlta", fechaAlta);
            result.put("fechaBaja", fechaBaja);
            return result;
        }
    }
}
