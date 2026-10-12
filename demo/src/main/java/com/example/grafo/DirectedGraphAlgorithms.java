package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.*;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {

    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        V sourceVertex = grafo.buscarVertice(source);
        Map<V, Double> distances = new HashMap<>();
        Map<V, V> predecessors = new HashMap<>();

        for (V v : grafo.vertices()) {
            distances.put(v, Double.POSITIVE_INFINITY);
        }

        if (sourceVertex == null) {
            return new DijkstraResultImpl<>(distances, predecessors, null);
        }

        distances.put(sourceVertex, 0.0);
        PriorityQueue<VertexDistance<V>> pq = new PriorityQueue<>(Comparator.comparingDouble(vd -> vd.distance));
        pq.add(new VertexDistance<>(sourceVertex, 0.0));

        Set<V> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            VertexDistance<V> current = pq.poll();
            V u = current.vertex;

            if (visited.contains(u)) continue;
            visited.add(u);

            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(u))) {
                V v = edge.target();
                if (visited.contains(v)) continue;

                double weight = edge.dato().getWeight();
                double newDist = distances.get(u) + weight;

                if (newDist < distances.get(v)) {
                    distances.put(v, newDist);
                    predecessors.put(v, u);
                    pq.add(new VertexDistance<>(v, newDist));
                }
            }
        }

        return new DijkstraResultImpl<>(distances, predecessors, sourceVertex);
    }

    private static class VertexDistance<V> {
        V vertex;
        double distance;

        VertexDistance(V vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    private static class DijkstraResultImpl<V> implements IDijkstraResult<V> {
        private final Map<V, Double> distances;
        private final Map<V, V> predecessors;
        private final V source;

        public DijkstraResultImpl(Map<V, Double> distances, Map<V, V> predecessors, V source) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.source = source;
        }

        @Override
        public double getCost(V otherVertex) {
            return distances.getOrDefault(otherVertex, Double.POSITIVE_INFINITY);
        }

        @Override
        public List<V> getPath(V otherVertex) {
            LinkedList<V> path = new LinkedList<>();
            if (source == null || !distances.containsKey(otherVertex) || distances.get(otherVertex) == Double.POSITIVE_INFINITY) {
                return path;
            }
            V current = otherVertex;
            while (current != null) {
                path.addFirst(current);
                if (current.equals(source)) break;
                current = predecessors.get(current);
            }
            if (path.isEmpty() || !path.getFirst().equals(source)) {
                return Collections.emptyList();
            }
            return path;
        }
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        Set<V> vertices = grafo.vertices();
        List<V> vertexList = new ArrayList<>(vertices);

        Map<V, Map<V, Double>> costMatrix = new HashMap<>();
        Map<V, Map<V, List<V>>> pathMatrix = new HashMap<>();

        for (V u : vertexList) {
            costMatrix.put(u, new HashMap<>());
            pathMatrix.put(u, new HashMap<>());
            for (V v : vertexList) {
                if (u.equals(v)) {
                    costMatrix.get(u).put(v, 0.0);
                    pathMatrix.get(u).put(v, new ArrayList<>(List.of(u)));
                } else {
                    costMatrix.get(u).put(v, Double.POSITIVE_INFINITY);
                    pathMatrix.get(u).put(v, new ArrayList<>());
                }
            }
        }

        for (Edge<V, D> edge : grafo.aristas()) {
            costMatrix.get(edge.source()).put(edge.target(), edge.dato().getWeight());
            pathMatrix.get(edge.source()).put(edge.target(), new ArrayList<>(List.of(edge.source(), edge.target())));
        }

        for (V k : vertexList) {
            for (V i : vertexList) {
                for (V j : vertexList) {
                    double costIK = costMatrix.get(i).get(k);
                    double costKJ = costMatrix.get(k).get(j);
                    if (costIK != Double.POSITIVE_INFINITY && costKJ != Double.POSITIVE_INFINITY) {
                        double newCost = costIK + costKJ;
                        if (newCost < costMatrix.get(i).get(j)) {
                            costMatrix.get(i).put(j, newCost);
                            List<V> pathItoK = pathMatrix.get(i).get(k);
                            List<V> pathKtoJ = pathMatrix.get(k).get(j);
                            if (!pathItoK.isEmpty() && !pathKtoJ.isEmpty()) {
                                List<V> combined = new ArrayList<>(pathItoK);
                                combined.addAll(pathKtoJ.subList(1, pathKtoJ.size()));
                                pathMatrix.get(i).put(j, combined);
                            }
                        }
                    }
                }
            }
        }

        return new FloydWarshallResultImpl<>(costMatrix, pathMatrix);
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        Set<V> vertices = grafo.vertices();
        List<V> vertexList = new ArrayList<>(vertices);

        Map<V, Map<V, Double>> reachMatrix = new HashMap<>();
        Map<V, Map<V, List<V>>> pathMatrix = new HashMap<>();

        for (V u : vertexList) {
            reachMatrix.put(u, new HashMap<>());
            pathMatrix.put(u, new HashMap<>());
            for (V v : vertexList) {
                if (u.equals(v)) {
                    reachMatrix.get(u).put(v, 0.0);
                    pathMatrix.get(u).put(v, new ArrayList<>(List.of(u)));
                } else {
                    reachMatrix.get(u).put(v, Double.POSITIVE_INFINITY);
                    pathMatrix.get(u).put(v, new ArrayList<>());
                }
            }
        }

        for (Edge<V, D> edge : grafo.aristas()) {
            reachMatrix.get(edge.source()).put(edge.target(), 1.0);
            pathMatrix.get(edge.source()).put(edge.target(), new ArrayList<>(List.of(edge.source(), edge.target())));
        }

        for (V k : vertexList) {
            for (V i : vertexList) {
                for (V j : vertexList) {
                    if (reachMatrix.get(i).get(k) != Double.POSITIVE_INFINITY && reachMatrix.get(k).get(j) != Double.POSITIVE_INFINITY) {
                        reachMatrix.get(i).put(j, 1.0);
                    }
                }
            }
        }

        return new FloydWarshallResultImpl<>(reachMatrix, pathMatrix);
    }

    private static class FloydWarshallResultImpl<V> implements IFloydWarshallResult<V> {
        private final Map<V, Map<V, Double>> costs;
        private final Map<V, Map<V, List<V>>> paths;

        public FloydWarshallResultImpl(Map<V, Map<V, Double>> costs, Map<V, Map<V, List<V>>> paths) {
            this.costs = costs;
            this.paths = paths;
        }

        @Override
        public List<V> getPath(V source, V target) {
            if (paths.containsKey(source) && paths.get(source).containsKey(target)) {
                return paths.get(source).get(target);
            }
            return Collections.emptyList();
        }

        @Override
        public double getCost(V source, V target) {
            if (costs.containsKey(source) && costs.get(source).containsKey(target)) {
                return costs.get(source).get(target);
            }
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public boolean connected(V source, V target) {
            return getCost(source, target) != Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        V center = null;
        double minEccentricity = Double.POSITIVE_INFINITY;

        for (V vertex : grafo.vertices()) {
            double eccentricity = obtenerExcentricidad(grafo, grafo.construirComparable(vertex));
            if (eccentricity < minEccentricity) {
                minEccentricity = eccentricity;
                center = vertex;
            }
        }
        return center;
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        IDijkstraResult<V> result = dijkstra(vertexCriteria, grafo);
        double maxCost = 0.0;

        for (V vertex : grafo.vertices()) {
            double cost = result.getCost(vertex);
            if (cost == Double.POSITIVE_INFINITY) {
                return Double.POSITIVE_INFINITY;
            }
            if (cost > maxCost) {
                maxCost = cost;
            }
        }
        return maxCost;
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> sourceCriteria, Comparable<V> targetCriteria, IGraph<V, D> grafo) {
        V source = grafo.buscarVertice(sourceCriteria);
        V target = grafo.buscarVertice(targetCriteria);
        List<Path<V>> allPaths = new ArrayList<>();

        if (source == null || target == null) return allPaths;

        dfsAllPaths(source, target, grafo, new ArrayList<>(), new HashSet<>(), 0.0, allPaths);
        return allPaths;
    }

    private <V, D> void dfsAllPaths(V current, V target, IGraph<V, D> grafo, List<V> currentPath, Set<V> visited, double currentCost, List<Path<V>> allPaths) {
        visited.add(current);
        currentPath.add(current);

        if (current.equals(target)) {
            allPaths.add(new Path<>(new ArrayList<>(currentPath), currentCost));
        } else {
            for (Edge<V, D> edge : grafo.adyacencias(grafo.construirComparable(current))) {
                V neighbor = edge.target();
                if (!visited.contains(neighbor)) {
                    double weight = edge.dato() instanceof WeightedEdge ? ((WeightedEdge) edge.dato()).getWeight() : 1.0;
                    dfsAllPaths(neighbor, target, grafo, currentPath, visited, currentCost + weight, allPaths);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V start = grafo.buscarVertice(sourceCriteria);
        if (start == null) return;

        Set<V> visited = new HashSet<>();
        dfsUtil(start, grafo, visited, consumer);
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
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Stack<V> stack = new Stack<>();
        Set<V> visited = new HashSet<>();

        for (V vertex : grafo.vertices()) {
            if (!visited.contains(vertex)) {
                topologicalSortUtil(vertex, grafo, visited, stack);
            }
        }

        List<V> topologicalOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            topologicalOrder.add(stack.pop());
        }
        return topologicalOrder;
    }

    private <V, D> void topologicalSortUtil(V vertex, IDirectedIGraph<V, D> grafo, Set<V> visited, Stack<V> stack) {
        visited.add(vertex);

        for (V successor : grafo.successors(grafo.construirComparable(vertex))) {
            if (!visited.contains(successor)) {
                topologicalSortUtil(successor, grafo, visited, stack);
            }
        }
        stack.push(vertex);
    }
}