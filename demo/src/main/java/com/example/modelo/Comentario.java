package com.example.modelo;

import java.time.LocalDateTime;
import com.example.entorno.*;

public class Comentario {
    private String id;
    private String contenido;
    private Usuario autor;
    private Posteo posteo;
    private LocalDateTime fecha;

    public Comentario(String id, String contenido, Usuario autor, Posteo posteo) {
        this.id = id;
        this.contenido = contenido;
        this.autor = autor;
        this.posteo = posteo;
        this.fecha = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public Usuario getAutor() {
        return autor;
    }

    public Posteo getPosteo() {
        return posteo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}