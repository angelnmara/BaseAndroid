package com.lamarrulla.baseandroid.models;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Date;

public class Dispositivo {
    @IgnoreExtraProperties
    public static class User{
        public String nombre;
        public String email;
        public String urlFoto;
        public String telefono;
        public User(){
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }
        public User(String nombre, String email, String urlFoto, String telefono){
            this.nombre = nombre;
            this.email = email;
            this.urlFoto = urlFoto;
            this.telefono = telefono;
        }
    }
    @IgnoreExtraProperties
    public static class DispositivoUsuario {
        public String dispositivo;
        public String usuario;
        public boolean activo;
        public Date fechaAlta;
        public Date fechaBaja;
        public DispositivoUsuario(){
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }
        public DispositivoUsuario(String dispositivo, String usuario, boolean activo, Date fechaAlta, Date fechaBaja){
            this.dispositivo = dispositivo;
            this.usuario = usuario;
            this.activo = activo;
            this.fechaAlta = fechaAlta;
            this.fechaBaja = fechaBaja;
        }
    }
    public static class DispositivosMarks{
        public String dispositivo;
        public Marker marker;
        public DispositivosMarks(){}
        public DispositivosMarks(String dispositivo, Marker marker){
            this.dispositivo = dispositivo;
            this.marker = marker;

        }
    }
}
