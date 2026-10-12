package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.*;
import java.util.function.Consumer;

public class UndirectedGraphAlgorithmImpl
        implements IUndirectedGraphAlgorithm {

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(
            IUndirectedGraph<V, D> graph) {

        IUndirectedGraph<V, D> result = graph.createEmptyGraph();

        for (V vertex : graph.vertices()) {
            result.addVertex(vertex);
        }

        List<Edge<V, D>> edges = new ArrayList<>(graph.edges());

        edges.sort(Comparator.comparingDouble(
                edge -> edge.getData().getWeight()
        ));

        Map<V, V> parent = new HashMap<>();

        for (V vertex : graph.vertices()) {
            parent.put(vertex, vertex);
        }

        for (Edge<V, D> edge : edges) {
            V source = edge.getSource();
            V target = edge.getTarget();

            V rootSource = find(parent, source);
            V rootTarget = find(parent, target);

            if (!rootSource.equals(rootTarget)) {
                result.addEdge(source, target, edge.getData());
                parent.put(rootSource, rootTarget);
            }
        }

        return result;
    }

    private <V> V find(Map<V, V> parent, V vertex) {
        V current = parent.get(vertex);

        if (!current.equals(vertex)) {
            current = find(parent, current);
            parent.put(vertex, current);
        }

        return current;
    }

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(
            IUndirectedGraph<V, D> graph,
            Comparable<V> source) {

        IUndirectedGraph<V, D> result = graph.createEmptyGraph();

        for (V vertex : graph.vertices()) {
            result.addVertex(vertex);
        }

        V start = null;

        for (V vertex : graph.vertices()) {
            if (graph.construirComparable(vertex).compareTo(
                    (V) source) == 0) {
                start = vertex;
                break;
            }
        }

        if (start == null) {
            return result;
        }

        List<V> visited = new ArrayList<>();
        visited.add(start);

        while (visited.size() < graph.vertices().size()) {
            List<V> outside = new ArrayList<>();

            for (V vertex : graph.vertices()) {
                if (!visited.contains(vertex)) {
                    outside.add(vertex);
                }
            }

            Edge<V, D> minEdge =
                    searchMinEdge(graph, visited, outside);

            if (minEdge == null) {
                break;
            }

            result.addEdge(
                    minEdge.getSource(),
                    minEdge.getTarget(),
                    minEdge.getData()
            );

            if (!visited.contains(minEdge.getSource())) {
                visited.add(minEdge.getSource());
            }

            if (!visited.contains(minEdge.getTarget())) {
                visited.add(minEdge.getTarget());
            }
        }

        return result;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(
            IUndirectedGraph<V, D> graph,
            Collection<V> U,
            Collection<V> V) {

        Edge<V, D> minEdge = null;
        double minWeight = Double.MAX_VALUE;

        for (V source : U) {
            for (V target : V) {
                Edge<V, D> edge = graph.getEdge(source, target);

                if (edge != null
                        && edge.getData().getWeight() < minWeight) {
                    minWeight = edge.getData().getWeight();
                    minEdge = edge;
                }
            }
        }

        return minEdge;
    }

    @Override
    public <V, D> void bea(
            IUndirectedGraph<V, D> graph,
            Consumer<V> consumer) {

        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        for (V start : graph.vertices()) {
            if (!visited.contains(start)) {
                visited.add(start);
                queue.add(start);

                while (!queue.isEmpty()) {
                    V current = queue.poll();
                    consumer.accept(current);

                    for (V neighbor : graph.neighbors(current)) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
    }
}