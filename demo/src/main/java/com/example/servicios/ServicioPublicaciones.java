package com.example.servicios;

import com.example.grafo.DirectedGraph;
import com.example.grafo.model.edge.Edge;
import com.example.modelo.Comentario;
import com.example.modelo.Mencion;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

//IMPORTS QUE NECESITO PARA LA CONSULTA 4
import com.example.grafo.Graph;
import com.example.grafo.IUndirectedGraph;
import com.example.grafo.UndirectedGraphAlgorithmImpl;
import com.example.grafo.model.edge.WeightedEdge;

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
        this.contadorPosteos = 0;
        this.contadorComentarios = 0;
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
                if (contenido.contains("@" + u.getNombreUsuario())) {
                    Mencion mencion = new Mencion(nuevoPosteo, u);
                    this.menciones.add(mencion);
                    nuevoPosteo.agregarMencion(u);          // <- AGREGO UNA NUEVA LINEA
                    sumarInteraccion(autor, u);
                }
            }
        }

        return nuevoPosteo;
    }

    public Comentario comentar(Usuario autor, Posteo posteo, String contenido, List<Usuario> todosLosUsuarios) {
        if (autor == null || posteo == null || contenido == null) {
            throw new IllegalArgumentException("Parametros no validos para comentar");
        }

        this.contadorComentarios++;
        String idComentario = "C-" + this.contadorComentarios;

        Comentario nuevoComentario = new Comentario(idComentario, contenido, autor, posteo);
        this.comentarios.add(nuevoComentario);
        posteo.agregarComentario(nuevoComentario);          // <- AGREGO
        sumarInteraccion(autor, posteo.getAutor());

        if (todosLosUsuarios != null) {
            for (Usuario u : todosLosUsuarios) {
                if (contenido.contains("@" + u.getNombreUsuario())) {
                    Mencion mencion = new Mencion(posteo, u);
                    this.menciones.add(mencion);
                    posteo.agregarMencion(u);               // <-AGERGO
                    sumarInteraccion(autor, u);
                }
            }
        }

        return nuevoComentario;
    }

    public void compartir(Usuario usuario, Posteo posteo) {
        if (usuario == null || posteo == null) {
            throw new IllegalArgumentException("Usuario o posteo no pueden ser nulos");
        }

        sumarInteraccion(usuario, posteo.getAutor());
    }

    public List<Posteo> construirFeed(Usuario usuario, Set<Usuario> seguidos) {
        if (usuario == null)
            return new ArrayList<>();

        List<Posteo> feed = new ArrayList<>();
        Set<Usuario> seguidosSet = (seguidos != null) ? seguidos : new HashSet<>();

        for (Posteo p : publicaciones) {
            boolean esPropio = p.getAutor().equals(usuario);
            boolean esDeSeguido = seguidosSet.contains(p.getAutor());
            boolean esMencionado = esUsuarioMencionadoEnPosteo(p, usuario);

            if (esPropio || esDeSeguido || esMencionado) {
                feed.add(p);
            }
        }
        return feed;
    }

    public int calcularAlcance(Posteo posteo) {
        if (posteo == null)
            return 0;

        Set<Usuario> alcanzados = new HashSet<>();
        Queue<Usuario> cola = new LinkedList<>();

        Usuario autorOriginal = posteo.getAutor();
        alcanzados.add(autorOriginal);
        cola.add(autorOriginal);

        for (Mencion m : menciones) {
            if (m.getPosteo().equals(posteo)) {
                Usuario mencionado = m.getUsuarioMencionado();
                if (alcanzados.add(mencionado)) {
                    cola.add(mencionado);
                }
            }
        }

        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();
            Comparable<Usuario> criterioActual = v -> v.equals(actual) ? 0 : -1;
            Set<Usuario> vecinos = grafoInteracciones.successors(criterioActual);
            if (vecinos != null) {
                for (Usuario vecino : vecinos) {
                    if (alcanzados.add(vecino)) {
                        cola.add(vecino);
                    }
                }
            }
        }

        return alcanzados.size();
    }

    private void sumarInteraccion(Usuario origen, Usuario destino) {
        if (origen == null || destino == null || origen.equals(destino))
            return;

        this.grafoInteracciones.agregarVertice(origen);
        this.grafoInteracciones.agregarVertice(destino);

        Comparable<Usuario> critOrigen = v -> v.equals(origen) ? 0 : -1;
        Comparable<Usuario> critDestino = v -> v.equals(destino) ? 0 : -1;

        if (this.grafoInteracciones.existeArista(critOrigen, critDestino)) {
            Edge<Usuario, Integer> arista = this.grafoInteracciones.obtenerArista(critOrigen, critDestino);

            int pesoActual = 0;
            if (arista != null && arista.dato() != null) {
                pesoActual = (Integer) arista.dato();
            }

            this.grafoInteracciones.agregarArista(origen, destino, pesoActual + 1);
        } else {
            this.grafoInteracciones.agregarArista(origen, destino, 1);
        }
    }

    private boolean esUsuarioMencionadoEnPosteo(Posteo posteo, Usuario usuario) {
        for (Mencion m : menciones) {
            if (m.getPosteo().equals(posteo) && m.getUsuarioMencionado().equals(usuario)) {
                return true;
            }
        }
        return false;
    }
        // =====================================================================
    // CONSULTA PROPIA 4: Red de vinculos mas fuertes
    // =====================================================================

    /**
     * Graph (de la biblioteca) es no dirigido pero no implementa IUndirectedGraph,
     * y Kruskal pide un IUndirectedGraph. Esta clase hereda todo de Graph
     * y solo le agrega ese tipo, sin modificar la biblioteca.
     */
    private static class GrafoNoDirigido<V, D> extends Graph<V, D> implements IUndirectedGraph<V, D> {
    }

    /**
     * Devuelve el conjunto minimo de relaciones que mantiene conectados a
     * los usuarios que interactuaron, quedandose con las de mas interaccion.
     * Se arma un grafo NO dirigido de interacciones (no importa quien
     * comento a quien, sino que hubo contacto) con peso 1 / interacciones:
     * mientras mas interaccion, menor el peso. Kruskal busca el arbol de
     * menor peso total, asi que elige los vinculos mas fuertes.
     * Complejidad: O(E log E).
     */
    public IUndirectedGraph<Usuario, WeightedEdge> redDeVinculosFuertes() {
        GrafoNoDirigido<Usuario, WeightedEdge> noDirigido = new GrafoNoDirigido<>();

        // Mismos usuarios que el grafo de interacciones
        for (Usuario u : grafoInteracciones.vertices()) {
            noDirigido.agregarVertice(u);
        }

        // Pasamos cada interaccion u -> v a una arista u - v
        for (Edge<Usuario, Integer> arista : grafoInteracciones.aristas()) {
            Usuario u = arista.source();
            Usuario v = arista.target();
            int total = arista.dato();

            // Si tambien hay interaccion v -> u, las sumamos (es el mismo vinculo)
            Edge<Usuario, Integer> vuelta = grafoInteracciones.obtenerArista(v, u);
            if (vuelta != null) {
                total += vuelta.dato();
            }

            // Mas interacciones = menor peso
            noDirigido.agregarArista(u, v, new WeightedEdge(1.0 / total));
        }

        // Kruskal devuelve el arbol (o bosque) de menor peso total
        UndirectedGraphAlgorithmImpl algoritmos = new UndirectedGraphAlgorithmImpl();
        return algoritmos.kruskal(noDirigido);
    }
}