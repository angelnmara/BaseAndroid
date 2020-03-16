package com.lamarrulla.baseandroid.models;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Date;
import java.util.List;

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

    public static class Fecha{
        public int date;
        public int hours;
        public int seconds;
        public int month;
        public int timezoneOffset;
        public int year;
        public int minutes;
        public long time;
        public int day;
        public Fecha(){
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }
        public Fecha(int date, int hours, int seconds, int month, int timezoneOffset, int year, int minutes, long time, int day){
            this.date = date;
            this.hours = hours;
            this.seconds = seconds;
            this.month = month;
            this.timezoneOffset = timezoneOffset;
            this.year = year;
            this.minutes = minutes;
            this.time = time;
            this.day = day;
        }
    }

    @IgnoreExtraProperties
    public static class DispositivoUsuario {
        public String dispositivo;
        public String usuario;
        public boolean activo;
        public Fecha fechaAlta;
        public Fecha fechaBaja;
        public boolean favorito;
        public boolean seleccionado;
        public Marker marker;

        public DispositivoUsuario(){
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }
        public DispositivoUsuario(String dispositivo, String usuario, boolean activo, Fecha fechaAlta, Fecha fechaBaja, boolean favorito, boolean seleccionado, Marker marker){
            this.dispositivo = dispositivo;
            this.usuario = usuario;
            this.activo = activo;
            this.fechaAlta = fechaAlta;
            this.fechaBaja = fechaBaja;
            this.favorito = favorito;
            this.seleccionado = seleccionado;
            this.marker = marker;
        }
    }

    /*public static class DispositivosMarks{
        public String dispositivo;
        public Marker marker;
        public boolean dispositivoSeleccionado;
        public DispositivosMarks(){}
        public DispositivosMarks(String dispositivo, Marker marker, boolean dispositivoSeleccionado){
            this.dispositivo = dispositivo;
            this.marker = marker;
            this.dispositivoSeleccionado = dispositivoSeleccionado;
        }
    }*/

    public static class MyLocation{
        public double latitude;
        public double longitude;
        public double altitude;
        public long elapsedRealtimeNanos;
        public long time;
        public float bearing;
        public float bearingAccuracyDegrees;
        public float speed;
        public float speedAccuracyMetersPerSecond;
        public float verticalAccuracyMeters;
        public float accuracy;
        public MyLocation(double latitude, double longitude, double altitude, long elapsedRealtimeNanos, long time, float bearing, float bearingAccuracyDegrees, float speed, float speedAccuracyMetersPerSecond, float verticalAccuracyMeters, float accuracy){
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.elapsedRealtimeNanos = elapsedRealtimeNanos;
            this.time = time;
            this.bearing = bearing;
            this.bearingAccuracyDegrees = bearingAccuracyDegrees;
            this.speed = speed;
            this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;
            this.verticalAccuracyMeters = verticalAccuracyMeters;
            this.accuracy = accuracy;
        }
    }
}
