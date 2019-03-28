package com.lamarrulla.baseandroid.models;

public class Login {
    public static class Parametro {
        public String nombreParametro;
        public String valorParametro;
        public Parametro(String nombreParametro, String valorParametro){
            this.nombreParametro = nombreParametro;
            this.valorParametro = valorParametro;
        }
    }
    public static class Menu{
        public String nombreMenu;
        public String imagenMenu;
        public int ordenMenu;

        public Menu(String nombreMenu, String imagenMenu, int ordenMenu){
            this.imagenMenu = imagenMenu;
            this.nombreMenu = nombreMenu;
            this.ordenMenu = ordenMenu;
        }
    }
}
