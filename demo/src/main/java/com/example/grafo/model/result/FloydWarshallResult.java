package com.example.grafo.model.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FloydWarshallResult<V> implements IFloydWarshallResult<V> {
    private final Map<V, Map<V, Double>> costs;
    private final Map<V, Map<V, List<V>>> paths;

    public FloydWarshallResult(Map<V, Map<V, Double>> costs, Map<V, Map<V, List<V>>> paths) {
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