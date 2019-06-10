package com.example.redsocial.DataBaseObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

public class User {

    private String nombre,
                    apellido,
                    correo,
                    imagenUrl;
    private ArrayList<String> amigos = new ArrayList<>(), fotos = new ArrayList<>(), datos = new ArrayList<>();

    public User() {
    }

    public User(String nombre, String apellido, String correo, String imagenUrl) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.imagenUrl = imagenUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public ArrayList<String> getAmigos() {
        return amigos;
    }

    public void setAmigos(ArrayList<String> amigos) {
        this.amigos = amigos;
    }

    public ArrayList<String> getFotos() {
        return fotos;
    }

    public void setFotos(ArrayList<String> fotos) {
        this.fotos = fotos;
    }

    public ArrayList<String> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<String> datos) {
        this.datos = datos;
    }

    public void addFoto(String url){
        fotos.add(url);
    }

    public void addAmigo(String amigo){
        amigos.add(amigo);
    }

    public void addDato(String dato){
        datos.add(dato);
    }

    @Override
    public String toString() {
        return "User{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", correo='" + correo + '\'' +
                ", imagenUrl='" + imagenUrl + '\'' +
                '}';
    }
}
