package com.example.servicios;

import com.example.grafo.DirectedGraph;
import com.example.grafo.DirectedGraphAlgorithms;
import com.example.modelo.Mencion;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

    public DirectedGraph<Usuario, Object> construirGrafoDifusion(Posteo posteo) {
        DirectedGraph<Usuario, Object> grafo = new DirectedGraph<>();
        Usuario autor = posteo.getAutor();
        grafo.agregarVertice(autor);

        // 1. Le llega a los seguidores del autor
        conectarConSeguidores(grafo, autor);

        // 2. Le llega a los mencionados, aunque no sigan al autor
        for (Mencion mencion : posteo.getMenciones()) {
            Usuario mencionado = mencion.getUsuarioMencionado();
            grafo.agregarVertice(mencionado);
            grafo.agregarArista(autor, mencionado, null);
        }

        // 3. Le llega a los seguidores de cada usuario que lo compartio
        for (Usuario compartidor : obtenerCompartidores(posteo)) {
            grafo.agregarVertice(compartidor);
            conectarConSeguidores(grafo, compartidor);
        }

        return grafo;
    }

    private void conectarConSeguidores(DirectedGraph<Usuario, Object> grafo, Usuario usuario) {
        for (Usuario seguidor : servicioSeguidores.obtenerSeguidores(usuario)) {
            grafo.agregarVertice(seguidor);
            grafo.agregarArista(usuario, seguidor, null);
        }
    }

    public Set<Usuario> calcularAlcance(Posteo posteo) {
        if (posteo == null) {
            return new HashSet<>();
        }
        DirectedGraph<Usuario, Object> grafo = construirGrafoDifusion(posteo);
        Set<Usuario> alcanzados = new HashSet<>();
        algoritmos.recorridoEnAmplitud(grafo, posteo.getAutor(), alcanzados::add);
        alcanzados.remove(posteo.getAutor());
        return alcanzados;
    }

    public boolean leLlega(Usuario usuario, Posteo posteo) {
        return posteo.getAutor().equals(usuario) || calcularAlcance(posteo).contains(usuario);
    }


    // CONSULTA 1: Camino de difusion
    
    public List<Usuario> caminoDeDifusion(Posteo posteo, Usuario destino) {
        DirectedGraph<Usuario, Object> grafo = construirGrafoDifusion(posteo);
        Usuario autor = posteo.getAutor();

        // padre.get(u) = usuario a traves del cual le llego el posteo a u
        Map<Usuario, Usuario> padre = new HashMap<>();
        Set<Usuario> visitados = new HashSet<>();
        Queue<Usuario> cola = new LinkedList<>();

        visitados.add(autor);
        cola.add(autor);

        // BFS: recorremos por niveles desde el autor
        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();

            // Si llegamos al destino, ya tenemos el camino mas corto
            if (actual.equals(destino)) {
                break;
            }

            for (Usuario siguiente : grafo.successors(actual)) {
                if (visitados.add(siguiente)) {
                    padre.put(siguiente, actual);
                    cola.add(siguiente);
                }
            }
        }

        // Si nunca lo visitamos, el posteo no le llego
        if (!visitados.contains(destino)) {
            return new ArrayList<>();
        }

        // Reconstruimos el camino yendo del destino hacia atras por los padres
        LinkedList<Usuario> camino = new LinkedList<>();
        for (Usuario u = destino; u != null; u = padre.get(u)) {
            camino.addFirst(u);
        }
        return camino;
    }

    
    // CONSULTA 5: Orden de llegada
    
    public List<Usuario> ordenDeLlegada(Posteo posteo) {
        DirectedGraph<Usuario, Object> grafo = construirGrafoDifusion(posteo);
        List<Usuario> orden = new ArrayList<>();

        // El BFS desde el autor agrega a cada usuario en el orden en que lo visita
        algoritmos.recorridoEnAmplitud(grafo, posteo.getAutor(), orden::add);

        // El autor no "recibe" su propio posteo
        orden.remove(posteo.getAutor());
        return orden;
    }
}