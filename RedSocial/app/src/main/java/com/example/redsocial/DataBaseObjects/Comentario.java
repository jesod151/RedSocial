package com.example.redsocial.DataBaseObjects;

public class Comentario {

    private String postId, comentario;

    public Comentario() {
    }

    public Comentario(String postId, String comentario) {
        this.postId = postId;
        this.comentario = comentario;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
