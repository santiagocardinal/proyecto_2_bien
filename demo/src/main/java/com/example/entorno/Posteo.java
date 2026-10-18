
package com.example.entorno;

import java.util.ArrayList;
import java.util.List;

public class Posteo {

    private String id;
    private String contenido;
    private List<Comentario> comentarios;
    private List<Menciones> menciones;

    public Posteo(String id, String contenido) {
        this.id = id;
        this.contenido = contenido;
        this.comentarios = new ArrayList<>();
        this.menciones = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public List<Comentario> getComentarios() {
        return new ArrayList<>(comentarios);
    }

    public List<Menciones> getMenciones() {
        return new ArrayList<>(menciones);
    }

    public void agregarComentario(Comentario comentario) {
        if (comentario != null) {
            comentarios.add(comentario);
        }
    }

    public void agregarMencion(Usuario usuario) {
        if (usuario != null) {
            Menciones mencion = new Menciones(this, usuario);
            menciones.add(mencion);
        }
    }
}
