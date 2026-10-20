package com.example.modelo;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comentario {

    private final String id;
    private final String contenido;
    private final Usuario autor;
    private final Posteo posteo;
    private final LocalDateTime fecha;

    public Comentario(String id, String contenido, Usuario autor, Posteo posteo, LocalDateTime fecha) {
        this.id = id;
        this.contenido = contenido;
        this.autor = Objects.requireNonNull(autor, "El comentario debe tener un autor");
        this.posteo = Objects.requireNonNull(posteo, "El comentario debe pertenecer a un posteo");
        this.fecha = Objects.requireNonNull(fecha, "El comentario debe tener una fecha");
    }

    public Comentario(String id, String contenido, Usuario autor, Posteo posteo) {
        this(id, contenido, autor, posteo, LocalDateTime.now());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comentario)) return false;
        Comentario otro = (Comentario) o;
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return id + " (" + autor.getNombreUsuario() + " en " + posteo.getId() + "): " + contenido;
    }
}