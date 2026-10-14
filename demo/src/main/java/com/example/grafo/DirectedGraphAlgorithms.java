package com.example.grafo;

import com.example.grafo.model.IGraph;
import com.example.grafo.model.edge.Edge;
import com.example.grafo.model.edge.WeightedEdge;
import com.example.grafo.model.result.DijkstraResult;
import com.example.grafo.model.result.FloydWarshallResult;
import com.example.grafo.model.result.IDijkstraResult;
import com.example.grafo.model.result.IFloydWarshallResult;
import com.example.grafo.model.result.Path;

import java.util.*;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {

    // ----------------------------------------------------------------- Dijkstra

    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        V sourceVertex = grafo.buscarVertice(source);
        Map<V, Double> distances = new HashMap<>();
        Map<V, V> predecessors = new HashMap<>();

        for (V v : grafo.vertices()) {
            distances.put(v, Double.POSITIVE_INFINITY);
        }

        if (sourceVertex == null) {
            return new DijkstraResult<>(distances, new HashMap<>());
        }

        distances.put(sourceVertex, 0.0);
        PriorityQueue<VertexDistance<V>> pq = new PriorityQueue<>(Comparator.comparingDouble(vd -> vd.distance));
        pq.add(new VertexDistance<>(sourceVertex, 0.0));
        Set<V> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            V u = pq.poll().vertex;
            if (!visited.add(u)) continue;

            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(u))) {
                V v = edge.target();
                if (visited.contains(v)) continue;

                double newDist = distances.get(u) + edge.dato().getWeight();
                if (newDist < distances.get(v)) {
                    distances.put(v, newDist);
                    predecessors.put(v, u);
                    pq.add(new VertexDistance<>(v, newDist));
                }
            }
        }

        // Reconstruir el camino de cada vértice alcanzable
        Map<V, List<V>> paths = new HashMap<>();
        for (V v : grafo.vertices()) {
            if (distances.get(v) == Double.POSITIVE_INFINITY) continue;
            LinkedList<V> path = new LinkedList<>();
            for (V cur = v; cur != null; cur = predecessors.get(cur)) {
                path.addFirst(cur);
                if (cur.equals(sourceVertex)) break;
            }
            paths.put(v, path);
        }

        return new DijkstraResult<>(distances, paths);
    }

    private static class VertexDistance<V> {
        final V vertex;
        final double distance;

        VertexDistance(V vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    // -------------------------------------------------------------------- Floyd

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        List<V> vertexList = new ArrayList<>(grafo.vertices());
        Map<V, Map<V, Double>> costMatrix = new HashMap<>();
        Map<V, Map<V, List<V>>> pathMatrix = new HashMap<>();
        inicializarMatrices(vertexList, costMatrix, pathMatrix);

        for (Edge<V, D> edge : grafo.aristas()) {
            V s = edge.source();
            V t = edge.target();
            double w = edge.dato().getWeight();
            // Un lazo (v -> v) no debe pisar el 0 de la diagonal
            if (w < costMatrix.get(s).get(t)) {
                costMatrix.get(s).put(t, w);
                pathMatrix.get(s).put(t, new ArrayList<>(List.of(s, t)));
            }
        }

        for (V k : vertexList) {
            for (V i : vertexList) {
                double costIK = costMatrix.get(i).get(k);
                if (costIK == Double.POSITIVE_INFINITY) continue;
                for (V j : vertexList) {
                    double costKJ = costMatrix.get(k).get(j);
                    if (costKJ == Double.POSITIVE_INFINITY) continue;
                    double newCost = costIK + costKJ;
                    if (newCost < costMatrix.get(i).get(j)) {
                        costMatrix.get(i).put(j, newCost);
                        pathMatrix.get(i).put(j, concatenar(pathMatrix.get(i).get(k), pathMatrix.get(k).get(j)));
                    }
                }
            }
        }

        return new FloydWarshallResult<>(costMatrix, pathMatrix);
    }

    // ------------------------------------------------------------------ Warshall

    /**
     * Cierre transitivo. El "costo" es 0 para (v,v), 1 si hay camino y +inf si no lo hay,
     * por lo que connected(...) indica alcanzabilidad. getPath devuelve un camino (no necesariamente el más corto).
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        List<V> vertexList = new ArrayList<>(grafo.vertices());
        Map<V, Map<V, Double>> reachMatrix = new HashMap<>();
        Map<V, Map<V, List<V>>> pathMatrix = new HashMap<>();
        inicializarMatrices(vertexList, reachMatrix, pathMatrix);

        for (Edge<V, D> edge : grafo.aristas()) {
            V s = edge.source();
            V t = edge.target();
            if (!s.equals(t)) {
                reachMatrix.get(s).put(t, 1.0);
                pathMatrix.get(s).put(t, new ArrayList<>(List.of(s, t)));
            }
        }

        for (V k : vertexList) {
            for (V i : vertexList) {
                if (reachMatrix.get(i).get(k) == Double.POSITIVE_INFINITY) continue;
                for (V j : vertexList) {
                    if (reachMatrix.get(i).get(j) == Double.POSITIVE_INFINITY
                            && reachMatrix.get(k).get(j) != Double.POSITIVE_INFINITY) {
                        reachMatrix.get(i).put(j, 1.0);
                        pathMatrix.get(i).put(j, concatenar(pathMatrix.get(i).get(k), pathMatrix.get(k).get(j)));
                    }
                }
            }
        }

        return new FloydWarshallResult<>(reachMatrix, pathMatrix);
    }

    private <V> void inicializarMatrices(List<V> vertexList,
                                         Map<V, Map<V, Double>> costMatrix,
                                         Map<V, Map<V, List<V>>> pathMatrix) {
        for (V u : vertexList) {
            Map<V, Double> fila = new HashMap<>();
            Map<V, List<V>> filaPaths = new HashMap<>();
            for (V v : vertexList) {
                if (u.equals(v)) {
                    fila.put(v, 0.0);
                    filaPaths.put(v, new ArrayList<>(List.of(u)));
                } else {
                    fila.put(v, Double.POSITIVE_INFINITY);
                    filaPaths.put(v, new ArrayList<>());
                }
            }
            costMatrix.put(u, fila);
            pathMatrix.put(u, filaPaths);
        }
    }

    /** Une el camino i->k con k->j sin repetir k. */
    private <V> List<V> concatenar(List<V> pathIK, List<V> pathKJ) {
        List<V> combined = new ArrayList<>(pathIK);
        combined.addAll(pathKJ.subList(1, pathKJ.size()));
        return combined;
    }

    // ------------------------------------------------- Centro y excentricidad

    /**
     * Centro del grafo: el vértice de menor excentricidad.
     * Si ningún vértice es alcanzable desde todos los demás, retorna null.
     */
    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        IFloydWarshallResult<V> floyd = floyd(grafo); // se calcula UNA sola vez
        V center = null;
        double minEccentricity = Double.POSITIVE_INFINITY;

        for (V vertex : grafo.vertices()) {
            double eccentricity = excentricidad(grafo, floyd, vertex);
            if (eccentricity < minEccentricity) {
                minEccentricity = eccentricity;
                center = vertex;
            }
        }
        return center;
    }

    /**
     * Excentricidad de v = max { costo del camino más corto de w a v | w en V }
     * (definición de Aho-Hopcroft-Ullman: se mira la COLUMNA de v en la matriz de Floyd).
     */
    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        V vertex = grafo.buscarVertice(vertexCriteria);
        if (vertex == null) return Double.POSITIVE_INFINITY;
        return excentricidad(grafo, floyd(grafo), vertex);
    }

    private <V> double excentricidad(IGraph<V, ?> grafo, IFloydWarshallResult<V> floyd, V vertex) {
        double max = 0.0;
        for (V w : grafo.vertices()) {
            double cost = floyd.getCost(w, vertex);
            if (cost > max) {
                max = cost; // si algún w no llega a v queda +infinito
            }
        }
        return max;
    }

    // ------------------------------------------------------- Todos los caminos

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> sourceCriteria, Comparable<V> targetCriteria, IGraph<V, D> grafo) {
        V source = grafo.buscarVertice(sourceCriteria);
        V target = grafo.buscarVertice(targetCriteria);
        List<Path<V>> allPaths = new ArrayList<>();
        if (source == null || target == null) return allPaths;

        dfsAllPaths(source, target, grafo, new ArrayList<>(), new HashSet<>(), 0.0, allPaths);
        return allPaths;
    }

    private <V, D extends WeightedEdge> void dfsAllPaths(V current, V target, IGraph<V, D> grafo, List<V> currentPath,
                                                         Set<V> visited, double currentCost, List<Path<V>> allPaths) {
        visited.add(current);
        currentPath.add(current);

        if (current.equals(target)) {
            allPaths.add(new Path<>(new ArrayList<>(currentPath), currentCost));
        } else {
            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(current))) {
                V neighbor = edge.target();
                if (!visited.contains(neighbor)) {
                    dfsAllPaths(neighbor, target, grafo, currentPath, visited,
                            currentCost + edge.dato().getWeight(), allPaths);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    // --------------------------------------------------------------- Recorridos

    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V start = grafo.buscarVertice(sourceCriteria);
        if (start == null) return;
        dfsUtil(start, grafo, new HashSet<>(), consumer);
    }

    private <V, D> void dfsUtil(V current, IGraph<V, D> grafo, Set<V> visited, Consumer<V> consumer) {
        visited.add(current);
        consumer.accept(current);
        for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(current))) {
            V neighbor = edge.target();
            if (!visited.contains(neighbor)) {
                dfsUtil(neighbor, grafo, visited, consumer);
            }
        }
    }

    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V start = grafo.buscarVertice(sourceCriteria);
        if (start == null) return;

        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            consumer.accept(current);
            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(current))) {
                V neighbor = edge.target();
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
    }

    // -------------------------------------------------- Clasificación topológica

    /**
     * Retorna el orden topológico. Si el grafo tiene ciclos no existe orden topológico
     * y se retorna una lista vacía.
     */
    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        if (grafo.tieneCiclos()) {
            return Collections.emptyList();
        }

        Deque<V> stack = new ArrayDeque<>();
        Set<V> visited = new HashSet<>();
        for (V vertex : grafo.vertices()) {
            if (!visited.contains(vertex)) {
                topologicalSortUtil(vertex, grafo, visited, stack);
            }
        }
        return new ArrayList<>(stack); // ArrayDeque usado como pila: itera desde el tope
    }

    private <V, D> void topologicalSortUtil(V vertex, IDirectedIGraph<V, D> grafo, Set<V> visited, Deque<V> stack) {
        visited.add(vertex);
        for (V successor : grafo.successors(grafo.construirComparable(vertex))) {
            if (!visited.contains(successor)) {
                topologicalSortUtil(successor, grafo, visited, stack);
            }
        }
        stack.push(vertex);
    }
}