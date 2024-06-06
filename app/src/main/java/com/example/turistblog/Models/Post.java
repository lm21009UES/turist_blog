package com.example.turistblog.Models;

import com.google.firebase.database.ServerValue;

public class Post {

    private String post_key;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private String imagen;
    private String user_foto;
    private String user_id;
    private Object time_stamp;

    public Post(String nombre, String descripcion, String ubicacion, String imagen, String user_foto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.imagen = imagen;
        this.user_foto = user_foto;
        this.time_stamp = ServerValue.TIMESTAMP;
    }

    public Post() {

    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getUser_foto() {
        return user_foto;
    }

    public String getUser_id() {
        return user_id;
    }

    public Object getTime_stamp() {
        return time_stamp;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setUser_foto(String user_foto) {
        this.user_foto = user_foto;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTime_stamp(Object time_stamp) {
        this.time_stamp = time_stamp;
    }

}
