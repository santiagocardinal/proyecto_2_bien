
package com.example.entorno;

public class Menciones {

    private Posteo posteo;
    private Usuario usuarioMencionado;

    public Menciones(Posteo posteo, Usuario usuarioMencionado) {
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
