package com.example.modelo;

import java.util.Objects;

public class Mencion {

    private final Posteo posteo;
    private final Usuario usuarioMencionado;

    public Mencion(Posteo posteo, Usuario usuarioMencionado) {
        this.posteo = Objects.requireNonNull(posteo, "La mención debe pertenecer a un posteo");
        this.usuarioMencionado = Objects.requireNonNull(usuarioMencionado, "La mención debe tener un usuario mencionado");
    }

    public Posteo getPosteo() {
        return posteo;
    }

    public Usuario getUsuarioMencionado() {
        return usuarioMencionado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mencion)) return false;
        Mencion otra = (Mencion) o;
        return posteo.equals(otra.posteo) && usuarioMencionado.equals(otra.usuarioMencionado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(posteo, usuarioMencionado);
    }

    @Override
    public String toString() {
        return "@" + usuarioMencionado.getNombreUsuario() + " en " + posteo.getId();
    }
}