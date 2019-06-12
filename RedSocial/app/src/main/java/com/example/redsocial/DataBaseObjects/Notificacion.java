package com.example.redsocial.DataBaseObjects;

import java.util.Date;

public class Notificacion {
    private String notifId, tipo, sender, senderImageUrl, receiver, texto;
    private Date fechaEnvio;
    private boolean leido;

    public Notificacion() {
    }

    public Notificacion(String notifId, String tipo, String sender, String receiver, String texto, Date fechaEnvio, boolean leido, String senderImageUrl) {
        this.notifId = notifId;
        this.tipo = tipo;
        this.sender = sender;
        this.receiver = receiver;
        this.texto = texto;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
        this.senderImageUrl = senderImageUrl;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }

    public String getNotifId() {
        return notifId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTexto() {
        return texto;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setNotifId(String notifId) {
        this.notifId = notifId;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
