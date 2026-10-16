package com.example.modelo;

public class Mencion {
    private Posteo posteo;
    private Usuario usuarioMencionado;

    public Mencion(Posteo posteo, Usuario usuarioMencionado) {
        this.posteo = posteo;
        this.usuarioMencionado = usuarioMencionado;
    }

    public Posteo getPosteo() {
        return posteo;
    }

    public Usuario getUsuarioMencionado() {
        return usuarioMencionado;
    }
}