package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.*;

public class Graph<V, D> implements IGraph<V, D> {

    private final Map<V, Map<V, Edge<V, D>>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex == null || adjacencyList.containsKey(vertex)) {
            return false;
        }
        adjacencyList.put(vertex, new HashMap<>());
        return true;
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
        if (criterio == null) return null;
        for (V v : adjacencyList.keySet()) {
            if (criterio.compareTo(v) == 0) {
                return v;
            }
        }
        return null;
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(target)) {
            return false;
        }
        
        // En un grafo no dirigido, creamos la arista y la asociamos en ambas direcciones
        Edge<V, D> edge = new Edge<>(source, target, dato); // O la clase de arista genérica que utilices
        adjacencyList.get(source).put(target, edge);
        adjacencyList.get(target).put(source, edge);
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V source = buscarVertice(sourceCriteria);
        V target = buscarVertice(targetCriteria);

        if (source == null || target == null) return false;

        Map<V, Edge<V, D>> sourceMap = adjacencyList.get(source);
        Map<V, Edge<V, D>> targetMap = adjacencyList.get(target);

        boolean removed = false;
        if (sourceMap != null && sourceMap.remove(target) != null) {
            removed = true;
        }
        if (targetMap != null && targetMap.remove(source) != null) {
            removed = true;
        }

        return removed;
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V vertexToRemove = buscarVertice(criteria);
        if (vertexToRemove == null) return false;

        // Remover de los mapas adyacentes de los vecinos
        for (V v : adjacencyList.keySet()) {
            adjacencyList.get(v).remove(vertexToRemove);
        }

        // Remover el vértice principal
        adjacencyList.remove(vertexToRemove);
        return true;
    }

    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }

    @Override
    public Set<Edge<V, D>> aristas() {
        Set<Edge<V, D>> allEdges = new HashSet<>();
        for (Map<V, Edge<V, D>> targets : adjacencyList.values()) {
            allEdges.addAll(targets.values());
        }
        return Collections.unmodifiableSet(allEdges);
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return obtenerArista(sourceCriteria, targetCriteria) != null;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V source = buscarVertice(sourceCriteria);
        V target = buscarVertice(targetCriteria);

        if (source == null || target == null) return null;

        Map<V, Edge<V, D>> targets = adjacencyList.get(source);
        if (targets == null) return null;

        return targets.get(target);
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V vertex = buscarVertice(verticeCriteria);
        if (vertex == null) return Collections.emptyList();

        Map<V, Edge<V, D>> targets = adjacencyList.get(vertex);
        if (targets == null) return Collections.emptyList();

        return new ArrayList<>(targets.values());
    }

    @Override
    public boolean esConexo() {
        if (adjacencyList.isEmpty()) return true;

        V start = adjacencyList.keySet().iterator().next();
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            Map<V, Edge<V, D>> neighbors = adjacencyList.get(current);
            if (neighbors != null) {
                for (V neighbor : neighbors.keySet()) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
        return visited.size() == adjacencyList.size();
    }

    @Override
    public void vaciar() {
        adjacencyList.clear();
    }

    @Override
    public boolean tieneCiclos() {
        Set<V> visited = new HashSet<>();
        for (V vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                if (tieneCiclosDFS(vertex, null, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tieneCiclosDFS(V current, V parent, Set<V> visited) {
        visited.add(current);

        Map<V, Edge<V, D>> neighbors = adjacencyList.get(current);
        if (neighbors != null) {
            for (V neighbor : neighbors.keySet()) {
                if (!visited.contains(neighbor)) {
                    if (tieneCiclosDFS(neighbor, current, visited)) {
                        return true;
                    }
                } else if (!neighbor.equals(parent)) {
                    return true;
                }
            }
        }
        return false;
    }
}