package com.example.grafo;

import com.example.grafo.model.edge.DirectedEdge;
import com.example.grafo.model.edge.Edge;

import java.util.*;

public class DirectedGraph<V, D> implements IDirectedIGraph<V, D> {

    private final Map<V, Map<V, Edge<V, D>>> adjacencyList;

    public DirectedGraph() {
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
        Edge<V, D> edge = new DirectedEdge<>(source, target, dato);
        adjacencyList.get(source).put(target, edge);
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V source = buscarVertice(sourceCriteria);
        V target = buscarVertice(targetCriteria);

        if (source == null || target == null) return false;

        Map<V, Edge<V, D>> targets = adjacencyList.get(source);
        if (targets != null && targets.remove(target) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V vertexToRemove = buscarVertice(criteria);
        if (vertexToRemove == null) return false;

        // Remover el vértice del mapa principal
        adjacencyList.remove(vertexToRemove);

        // Remover todas las aristas entrantes hacia este vértice desde otros vértices
        for (Map<V, Edge<V, D>> targets : adjacencyList.values()) {
            targets.remove(vertexToRemove);
        }

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
        
        // En un grafo dirigido, evaluamos conectividad débil (alcanzabilidad ignorando dirección de aristas)
        V start = adjacencyList.keySet().iterator().next();
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            
            Set<V> neighbors = new HashSet<>(successors(construirComparable(current)));
            neighbors.addAll(predecessors(construirComparable(current)));

            for (V neighbor : neighbors) {
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
        Set<V> recStack = new HashSet<>();

        for (V vertex : adjacencyList.keySet()) {
            if (tieneCiclosUtil(vertex, visited, recStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneCiclosUtil(V vertex, Set<V> visited, Set<V> recStack) {
        if (recStack.contains(vertex)) return true;
        if (visited.contains(vertex)) return false;

        visited.add(vertex);
        recStack.add(vertex);

        for (V successor : successors(construirComparable(vertex))) {
            if (tieneCiclosUtil(successor, visited, recStack)) {
                return true;
            }
        }

        recStack.remove(vertex);
        return false;
    }

    // --- MÉTODOS ESPECÍFICOS DE IDirectedIGraph ---

    @Override
    public Set<V> successors(Comparable<V> criteria) {
        V vertex = buscarVertice(criteria);
        if (vertex == null) return Collections.emptySet();

        Map<V, Edge<V, D>> targets = adjacencyList.get(vertex);
        if (targets == null) return Collections.emptySet();

        return Collections.unmodifiableSet(targets.keySet());
    }

    @Override
    public Set<V> predecessors(Comparable<V> criteria) {
        V vertex = buscarVertice(criteria);
        if (vertex == null) return Collections.emptySet();

        Set<V> preds = new HashSet<>();
        for (Map.Entry<V, Map<V, Edge<V, D>>> entry : adjacencyList.entrySet()) {
            if (entry.getValue().containsKey(vertex)) {
                preds.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(preds);
    }
}