package com.example.servicios;

import java.util.*;

import com.example.grafo.IDirectedIGraph;
import com.example.modelo.Usuario;

//IMPORTAS PARA REALIZAR LA CONSULTA 3
import com.example.grafo.DirectedGraph;
import com.example.grafo.DirectedGraphAlgorithms;
import com.example.grafo.model.edge.WeightedEdge;

public class ServicioAnalisisRed<D> {

    private IDirectedIGraph<Usuario, D> grafoSeguidores;

    public ServicioAnalisisRed(IDirectedIGraph<Usuario, D> grafoSeguidores) {
        this.grafoSeguidores = grafoSeguidores;
    }



    public Map<Usuario, Integer> calcularInfluencia() {

        Map<Usuario, Integer> influencia = new HashMap<>();

        for (Usuario usuario : grafoSeguidores.vertices()) {

            int seguidores = grafoSeguidores.predecessors(
                    grafoSeguidores.construirComparable(usuario)).size();

            influencia.put(usuario, seguidores);
        }

        return influencia;
    }

  
    public List<Set<Usuario>> detectarComunidades() {

        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();

        // 1. Para cada usuario, guardamos a quienes puede llegar (incluido el mismo)
        Map<Usuario, Set<Usuario>> alcanzables = new HashMap<>();
        for (Usuario usuario : grafoSeguidores.vertices()) {
            Set<Usuario> desdeUsuario = new HashSet<>();
            algoritmos.recorridoEnProfundidad(grafoSeguidores, usuario, desdeUsuario::add);
            alcanzables.put(usuario, desdeUsuario);
        }

        // 2. Armamos las comunidades
        Set<Usuario> asignados = new HashSet<>();
        List<Set<Usuario>> comunidades = new ArrayList<>();

        for (Usuario usuario : grafoSeguidores.vertices()) {

            // Si ya esta en una comunidad, lo salteamos
            if (asignados.contains(usuario)) {
                continue;
            }

            // Su comunidad: los que alcanza y que tambien lo alcanzan a el
            Set<Usuario> comunidad = new HashSet<>();
            for (Usuario otro : alcanzables.get(usuario)) {
                if (alcanzables.get(otro).contains(usuario)) {
                    comunidad.add(otro);
                }
            }

            asignados.addAll(comunidad);
            comunidades.add(comunidad);
        }

        return comunidades;
    }

    private void dfsOrden(Usuario usuario, Set<Usuario> visitados, Deque<Usuario> pila) {

        visitados.add(usuario);

        for (Usuario vecino : grafoSeguidores.successors(grafoSeguidores.construirComparable(usuario))) {
            if (!visitados.contains(vecino)) {
                dfsOrden(vecino, visitados, pila);
            }
        }
        pila.push(usuario);
    }

    private void dfsTranspuesto(Usuario usuario, Set<Usuario> visitados, Set<Usuario> comunidad) {

        visitados.add(usuario);
        comunidad.add(usuario);

        for (Usuario vecino : grafoSeguidores.predecessors(grafoSeguidores.construirComparable(usuario))) {
            if (!visitados.contains(vecino)) {
                dfsTranspuesto(vecino, visitados, comunidad);
            }
        }
    }



    public Set<Usuario> identificarUsuariosPuente() {

        Map<Usuario, Set<Usuario>> vecinos = new HashMap<>();

        // Construyo conexiones sin dirección
        for (Usuario usuario : grafoSeguidores.vertices()) {
            vecinos.put(usuario, new HashSet<>());
        }

        for (Usuario usuario : grafoSeguidores.vertices()) {

            for (Usuario seguido : grafoSeguidores.successors(grafoSeguidores.construirComparable(usuario))) {
                vecinos.get(usuario).add(seguido);
                vecinos.get(seguido).add(usuario);
            }
        }

        Set<Usuario> visitados = new HashSet<>();
        Set<Usuario> puentes = new HashSet<>();

        Map<Usuario, Integer> descubrimiento = new HashMap<>();
        Map<Usuario, Integer> low = new HashMap<>();

        int[] tiempo = { 0 };

        // Explorar todas las componentes del grafo
        for (Usuario usuario : grafoSeguidores.vertices()) {

            if (!visitados.contains(usuario)) {
                dfsPuentes(usuario, null, vecinos, visitados, puentes, descubrimiento, low, tiempo);
            }
        }

        return puentes;
    }

    private void dfsPuentes(Usuario actual, Usuario padre, Map<Usuario, Set<Usuario>> vecinos, Set<Usuario> visitados,
            Set<Usuario> puentes, Map<Usuario, Integer> descubrimiento, Map<Usuario, Integer> low, int[] tiempo) {

        visitados.add(actual);

        descubrimiento.put(actual, tiempo[0]);
        low.put(actual, tiempo[0]);
        tiempo[0]++;

        int hijos = 0;

        for (Usuario vecino : vecinos.get(actual)) {

            if (!visitados.contains(vecino)) {
                hijos++;

                dfsPuentes(vecino, actual, vecinos, visitados, puentes, descubrimiento, low, tiempo);

                low.put(actual, Math.min(low.get(actual), low.get(vecino)));

                // Si no es raíz y su hijo no puede alcanzar un ancestro de actual
                if (padre != null && low.get(vecino) >= descubrimiento.get(actual)) {
                    puentes.add(actual);
                }

            } else if (!vecino.equals(padre)) {

                low.put(actual, Math.min(low.get(actual), descubrimiento.get(vecino)));
            }
        }

        // La raíz es punto de articulación si tiene más de un hijo en el árbol DFS
        if (padre == null && hijos > 1) {
            puentes.add(actual);
        }
    }


    // CONSULTA 3: Centro de cada comunidad

    public Map<Set<Usuario>, Usuario> centrosDeComunidades() {
        Map<Set<Usuario>, Usuario> centros = new LinkedHashMap<>();

        // Para cada comunidad detectada, calculamos su centro
        for (Set<Usuario> comunidad : detectarComunidades()) {
            centros.put(comunidad, centroDeComunidad(comunidad));
        }
        return centros;
    }

    private Usuario centroDeComunidad(Set<Usuario> comunidad) {
        // Armamos un grafo solo con los usuarios de la comunidad
        DirectedGraph<Usuario, WeightedEdge> subgrafo = new DirectedGraph<>();
        for (Usuario u : comunidad) {
            subgrafo.agregarVertice(u);
        }

        // Copiamos los seguimientos que quedan dentro de la comunidad
        for (Usuario u : comunidad) {
            for (Usuario seguido : grafoSeguidores.successors(grafoSeguidores.construirComparable(u))) {
                if (comunidad.contains(seguido)) {
                    subgrafo.agregarArista(u, seguido, new WeightedEdge(1.0));
                }
            }
        }

        // calcula el vertice de menor excentricidad
        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();
        return algoritmos.obtenerCentroGrafo(subgrafo);
    }

}