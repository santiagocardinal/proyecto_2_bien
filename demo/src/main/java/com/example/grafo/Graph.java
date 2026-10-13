package grafo;

import grafo.model.IGraph;
import grafo.model.edge.Edge;
import grafo.model.edge.UndirectedEdge;

import java.util.*;

/**
 * Grafo NO dirigido implementado con lista de adyacencias.
 * Cada arista {u,v} se guarda dos veces: en el mapa de u como (u -> v)
 * y en el mapa de v como (v -> u), para que adyacencias(x) siempre
 * devuelva aristas cuyo source() es x (como pide IGraph).
 */
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
        if (source == null || target == null
                || !adjacencyList.containsKey(source) || !adjacencyList.containsKey(target)) {
            return false;
        }
        // Una arista "orientada" desde cada extremo; UndirectedEdge es simétrica en equals/hashCode,
        // así que en aristas() ambas cuentan como una sola.
        adjacencyList.get(source).put(target, new UndirectedEdge<>(source, target, dato));
        adjacencyList.get(target).put(source, new UndirectedEdge<>(target, source, dato));
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V source = buscarVertice(sourceCriteria);
        V target = buscarVertice(targetCriteria);
        if (source == null || target == null) return false;

        boolean removed = adjacencyList.get(source).remove(target) != null;
        removed |= adjacencyList.get(target).remove(source) != null;
        return removed;
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V vertexToRemove = buscarVertice(criteria);
        if (vertexToRemove == null) return false;

        // Quitar las aristas que llegan desde los vecinos
        for (V neighbor : adjacencyList.get(vertexToRemove).keySet()) {
            adjacencyList.get(neighbor).remove(vertexToRemove);
        }
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
        return adjacencyList.get(source).get(target);
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V vertex = buscarVertice(verticeCriteria);
        if (vertex == null) return Collections.emptyList();
        return new ArrayList<>(adjacencyList.get(vertex).values());
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
            for (V neighbor : adjacencyList.get(current).keySet()) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
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
            if (!visited.contains(vertex) && tieneCiclosDFS(vertex, null, visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneCiclosDFS(V current, V parent, Set<V> visited) {
        visited.add(current);
        for (V neighbor : adjacencyList.get(current).keySet()) {
            if (!visited.contains(neighbor)) {
                if (tieneCiclosDFS(neighbor, current, visited)) {
                    return true;
                }
            } else if (!neighbor.equals(parent)) {
                // Vecino ya visitado que no es el padre (incluye lazos v-v) => ciclo
                return true;
            }
        }
        return false;
    }
}