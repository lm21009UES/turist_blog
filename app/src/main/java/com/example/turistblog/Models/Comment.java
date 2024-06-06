package com.example.turistblog.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content, uid, uimg, uname;
    private float calificacion;
    private Object timestamp;

    public Comment() {

    }

    public Comment(String content, String uid, String uimg, String uname, float calificacion) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.calificacion = calificacion;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Comment(String content, String uid, String uimg, String uname, Object timestamp) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

}
