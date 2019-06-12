package com.example.redsocial.DataBaseObjects;

import java.util.Date;

public class Friend {
    private String notifId, friend, user;
    private Date fechaAmistad;

    public Friend() {
    }

    public Friend(String notifId, String friend, String user, Date fechaAmistad) {
        this.notifId = notifId;
        this.friend = friend;
        this.user = user;
        this.fechaAmistad = fechaAmistad;
    }

    public String getNotifId() {
        return notifId;
    }

    public void setNotifId(String notifId) {
        this.notifId = notifId;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getFechaAmistad() {
        return fechaAmistad;
    }

    public void setFechaAmistad(Date fechaAmistad) {
        this.fechaAmistad = fechaAmistad;
    }
}
