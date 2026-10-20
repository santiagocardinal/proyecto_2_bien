package com.example.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Posteo {

    private final String id;
    private final String contenido;
    private final Usuario autor;
    private final List<Comentario> comentarios;
    private final List<Mencion> menciones;

    public Posteo(String id, String contenido, Usuario autor) {
        this.id = id;
        this.contenido = contenido;
        this.autor = Objects.requireNonNull(autor, "El posteo debe tener un autor");
        this.comentarios = new ArrayList<>();
        this.menciones = new ArrayList<>();
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

    public List<Comentario> getComentarios() {
        return new ArrayList<>(comentarios);
    }

    public List<Mencion> getMenciones() {
        return new ArrayList<>(menciones);
    }

    public void agregarComentario(Comentario comentario) {
        if (comentario != null) {
            comentarios.add(comentario);
        }
    }

    public void agregarMencion(Usuario usuario) {
        if (usuario != null && !fueMencionado(usuario)) {
            menciones.add(new Mencion(this, usuario));
        }
    }

    public boolean fueMencionado(Usuario usuario) {
        for (Mencion m : menciones) {
            if (m.getUsuarioMencionado().equals(usuario)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posteo)) return false;
        Posteo otro = (Posteo) o;
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return id + " (" + autor.getNombreUsuario() + "): " + contenido;
    }
}