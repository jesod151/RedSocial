package com.example.redsocial.DataBaseObjects;

import java.util.Date;

public class Post {

    private String userID, texto, imagenUrl;
    private int likes, dislikes;
    private Date fechaCreacion;

    public Post() {
    }

    public Post(String userID, String texto, String imagenUrl, int likes, int dislikes, Date fechaCreacion) {
        this.userID = userID;
        this.texto = texto;
        this.imagenUrl = imagenUrl;
        this.likes = likes;
        this.dislikes = dislikes;
        this.fechaCreacion = fechaCreacion;
    }

    public Post(String userID, String texto, int likes, int dislikes, Date fechaCreacion) {
        this.userID = userID;
        this.texto = texto;
        this.likes = likes;
        this.imagenUrl = "";
        this.dislikes = dislikes;
        this.fechaCreacion = fechaCreacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void like(){
        likes++;
    }

    public void dislike(){
        dislikes++;
    }
}
