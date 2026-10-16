package com.example.servicio;

import java.util.ArrayList;
import java.util.List;

import com.example.grafo.DirectedGraph;
import com.example.grafo.model.edge.Edge;
import com.example.modelo.Comentario;
import com.example.modelo.Mencion;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

public class ServicioPublicaciones {
    private final DirectedGraph<Usuario, Integer> grafoInteracciones;
    private final List<Posteo> publicaciones;
    private final List<Comentario> comentarios;
    private final List<Mencion> menciones;

    private int contadorPosteos;
    private int contadorComentarios;

    public ServicioPublicaciones() {
        this.grafoInteracciones = new DirectedGraph<>();
        this.publicaciones = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.menciones = new ArrayList<>();
        this.contadorComentarios = 0;
        this.contadorPosteos = 0;
    }

    public DirectedGraph<Usuario, Integer> getGrafoInteracciones() {
        return grafoInteracciones;
    }

    public List<Posteo> getPublicaciones() {
        return publicaciones;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public List<Mencion> getMenciones() {
        return menciones;
    }

    public int getContadorPosteos() {
        return contadorPosteos;
    }

    public void setContadorPosteos(int contadorPosteos) {
        this.contadorPosteos = contadorPosteos;
    }

    public int getContadorComentarios() {
        return contadorComentarios;
    }

    public void setContadorComentarios(int contadorComentarios) {
        this.contadorComentarios = contadorComentarios;
    }

    public Posteo publicar(Usuario autor, String contenido, List<Usuario> todosLosUsuarios) {
        if (autor == null || contenido == null) {
            throw new IllegalArgumentException("Autor y contenido no pueden ser nulos");
        }
        this.contadorPosteos++;
        String idPosteo = "P-" + this.contadorPosteos;

        Posteo nuevoPosteo = new Posteo(idPosteo, contenido, autor);
        this.publicaciones.add(nuevoPosteo);

        if (todosLosUsuarios != null) {
            for (Usuario u : todosLosUsuarios) {
                if (contenido.contains("@" + u.getNombre()) || contenido.contains("@" + u.getId())) {
                    Mencion mencion = new Mencion(nuevoPosteo, u);
                    this.menciones.add(mencion);

                    sumarInteraccion(autor, u);
                }

            }
        }
        return nuevoPosteo;
    }

    private void sumarInteraccion(Usuario origen, Usuario destino) {
        if (origen == null || destino == null || origen.equals(destino))
            return;

        this.grafoInteracciones.agregarVertice(origen);
        this.grafoInteracciones.agregarVertice(destino);

        Comparable<Usuario> critOrigen = v -> v.equals(origen) ? 0 : -1;
        Comparable<Usuario> critDestino = v -> v.equals(destino) ? 0 : -1;

        if (this.grafoInteracciones.existeArista(critOrigen, critDestino)) {
            var arista = this.grafoInteracciones.obtenerArista(critOrigen, critDestino);

            int pesoActual = 0;
            if (arista != null && arista.getDato() != null) {
                pesoActual = (Integer) arista.getDato();
            }

            this.grafoInteracciones.agregarArista(origen, destino, pesoActual + 1);
        } else {
            this.grafoInteracciones.agregarArista(origen, destino, 1);
        }
    }

}
