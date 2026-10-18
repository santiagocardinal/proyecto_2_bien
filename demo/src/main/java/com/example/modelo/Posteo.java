package com.example.modelo;

import com.example.entorno.Usuario;

public class Posteo {
    private String id;
    private String contenido;
    private Usuario autor;

    public Posteo(String id, String contenido, Usuario autor) {
        this.id = id;
        this.contenido = contenido;
        this.autor = autor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;

    }
}
