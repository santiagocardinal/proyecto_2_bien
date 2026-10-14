package com.example.grafo;

import com.example.grafo.model.edge.Edge;
import com.example.grafo.model.edge.WeightedEdge;

import java.util.*;
import java.util.function.Consumer;

public class UndirectedGraphAlgorithmImpl implements IUndirectedGraphAlgorithm {

    /**
     * Graph implementa IGraph; para poder devolver un IUndirectedGraph en kruskal/prim
     * se usa esta subclase vacía: hereda todo de Graph y solo agrega el tipo IUndirectedGraph.
     */
    private static class UndirectedGraph<V, D> extends Graph<V, D> implements IUndirectedGraph<V, D> {
    }

    // ------------------------------------------------------------------ Kruskal

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {
        IUndirectedGraph<V, D> result = new UndirectedGraph<>();
        result.agregarVertices(graph.vertices());

        // aristas() ya devuelve cada arista no dirigida una sola vez
        List<Edge<V, D>> edges = new ArrayList<>(graph.aristas());
        edges.sort(Comparator.comparingDouble(edge -> edge.dato().getWeight()));

        // Union-Find (conjuntos disjuntos) para detectar ciclos
        Map<V, V> parent = new HashMap<>();
        for (V vertex : graph.vertices()) {
            parent.put(vertex, vertex);
        }

        int aristasNecesarias = graph.cantidadDeVertices() - 1;
        int aristasAgregadas = 0;

        for (Edge<V, D> edge : edges) {
            if (aristasAgregadas == aristasNecesarias) break;

            V rootSource = find(parent, edge.source());
            V rootTarget = find(parent, edge.target());

            if (!rootSource.equals(rootTarget)) {
                result.agregarArista(edge.source(), edge.target(), edge.dato());
                parent.put(rootSource, rootTarget);
                aristasAgregadas++;
            }
        }
        return result;
    }

    private <V> V find(Map<V, V> parent, V vertex) {
        V current = parent.get(vertex);
        if (!current.equals(vertex)) {
            current = find(parent, current);
            parent.put(vertex, current); // compresión de caminos
        }
        return current;
    }

    // --------------------------------------------------------------------- Prim

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(IUndirectedGraph<V, D> graph, Comparable<V> source) {
        IUndirectedGraph<V, D> result = new UndirectedGraph<>();
        result.agregarVertices(graph.vertices());

        V start = graph.buscarVertice(source);
        if (start == null) {
            return result;
        }

        Set<V> U = new HashSet<>();          // vértices ya incluidos en el árbol
        Set<V> resto = new HashSet<>(graph.vertices());
        U.add(start);
        resto.remove(start);

        while (!resto.isEmpty()) {
            Edge<V, D> minEdge = searchMinEdge(graph, U, resto);
            if (minEdge == null) {
                break; // grafo no conexo: no se puede seguir expandiendo
            }
            // source() está en U y target() en resto (ver searchMinEdge)
            result.agregarArista(minEdge.source(), minEdge.target(), minEdge.dato());
            U.add(minEdge.target());
            resto.remove(minEdge.target());
        }
        return result;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(IUndirectedGraph<V, D> graph,
                                                                 Collection<V> U, Collection<V> V) {
        Edge<V, D> minEdge = null;
        double minWeight = Double.POSITIVE_INFINITY;

        for (V u : U) {
            // adyacencias(u) devuelve aristas con source() == u
            for (Edge<V, D> edge : graph.adyacencias(graph.construirComparable(u))) {
                if (V.contains(edge.target())) {
                    double w = edge.dato().getWeight();
                    if (w < minWeight) {
                        minWeight = w;
                        minEdge = edge;
                    }
                }
            }
        }
        return minEdge;
    }

    // ---------------------------------------------------------------------- BEA

    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        // Se recorre cada componente conexa
        for (V start : graph.vertices()) {
            if (visited.contains(start)) continue;

            visited.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                V current = queue.poll();
                consumer.accept(current);

                for (Edge<V, D> edge : graph.adyacencias(graph.construirComparable(current))) {
                    V neighbor = edge.target();
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }
}