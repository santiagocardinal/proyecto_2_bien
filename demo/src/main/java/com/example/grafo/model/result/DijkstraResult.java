package grafo.model.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DijkstraResult<V> implements IDijkstraResult<V> {
    private final Map<V, Double> costs;
    private final Map<V, List<V>> paths;

    public DijkstraResult(Map<V, Double> costs, Map<V, List<V>> paths) {
        this.costs = costs;
        this.paths = paths;
    }

    @Override
    public double getCost(V otherVertex) {
        return costs.getOrDefault(otherVertex, Double.POSITIVE_INFINITY);
    }

    @Override
    public List<V> getPath(V otherVertex) {
        return paths.getOrDefault(otherVertex, Collections.emptyList());
    }
}