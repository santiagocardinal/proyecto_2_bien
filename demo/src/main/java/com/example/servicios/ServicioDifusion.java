package com.example.servicios;

import com.example.grafo.DirectedGraph;
import com.example.grafo.DirectedGraphAlgorithms;
import com.example.grafo.model.edge.WeightedEdge;
import com.example.modelo.Mencion;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ServicioDifusion {

    private final ServicioSeguidores servicioSeguidores;
    private final Map<Posteo, Set<Usuario>> compartidos;
    private final DirectedGraphAlgorithms algoritmos;

    public ServicioDifusion(ServicioSeguidores servicioSeguidores) {
        this.servicioSeguidores = servicioSeguidores;
        this.compartidos = new HashMap<>();
        this.algoritmos = new DirectedGraphAlgorithms();
    }

    // Compartidos

    public void registrarCompartido(Usuario usuario, Posteo posteo) {
        if (usuario == null || posteo == null) {
            throw new IllegalArgumentException("Usuario y posteo no pueden ser nulos");
        }
        compartidos.putIfAbsent(posteo, new LinkedHashSet<>());
        compartidos.get(posteo).add(usuario);
    }

    public Set<Usuario> obtenerCompartidores(Posteo posteo) {
        Set<Usuario> resultado = compartidos.get(posteo);
        if (resultado == null) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(resultado);
    }

    public boolean fueCompartidoPorAlguno(Posteo posteo, Collection<Usuario> usuarios) {
        for (Usuario compartidor : obtenerCompartidores(posteo)) {
            if (usuarios.contains(compartidor)) {
                return true;
            }
        }
        return false;
    }

    // Grafo de difusión

    /**
     * Construye el grafo de difusión de un posteo.
     * Arista u -> v: el posteo le llegó a v a través de u.
     */
    public DirectedGraph<Usuario, WeightedEdge> construirGrafoDifusion(Posteo posteo) {
        DirectedGraph<Usuario, WeightedEdge> grafo = new DirectedGraph<>();
        Usuario autor = posteo.getAutor();
        grafo.agregarVertice(autor);

        // 1. Le llega a los seguidores del autor
        conectarConSeguidores(grafo, autor);

        // 2. Le llega a los mencionados, aunque no sigan al autor
        for (Mencion mencion : posteo.getMenciones()) {
            Usuario mencionado = mencion.getUsuarioMencionado();
            grafo.agregarVertice(mencionado);
            grafo.agregarArista(autor, mencionado, new WeightedEdge(1.0));
        }

        // 3. Le llega a los seguidores de cada usuario que lo compartió
        for (Usuario compartidor : obtenerCompartidores(posteo)) {
            grafo.agregarVertice(compartidor);
            conectarConSeguidores(grafo, compartidor);
        }

        return grafo;
    }

    private void conectarConSeguidores(DirectedGraph<Usuario, WeightedEdge> grafo, Usuario usuario) {
        for (Usuario seguidor : servicioSeguidores.obtenerSeguidores(usuario)) {
            grafo.agregarVertice(seguidor);
            grafo.agregarArista(usuario, seguidor, new WeightedEdge(1.0));
        }
    }

    // Alcance de los comentaris/posteos REVISAR ESTE PRIMER AVANCE

    /**
     * Usuarios a los que llega el posteo (sin contar al autor).
     * BFS desde el autor sobre el grafo de difusión: O(V + E).
     */
    public Set<Usuario> calcularAlcance(Posteo posteo) {
        if (posteo == null) {
            return new HashSet<>();
        }
        DirectedGraph<Usuario, WeightedEdge> grafo = construirGrafoDifusion(posteo);

        Set<Usuario> alcanzados = new HashSet<>();
        algoritmos.recorridoEnAmplitud(grafo, posteo.getAutor(), alcanzados::add);

        alcanzados.remove(posteo.getAutor());
        return alcanzados;
    }

    public boolean leLlega(Usuario usuario, Posteo posteo) {
        return posteo.getAutor().equals(usuario) || calcularAlcance(posteo).contains(usuario);
    }
}