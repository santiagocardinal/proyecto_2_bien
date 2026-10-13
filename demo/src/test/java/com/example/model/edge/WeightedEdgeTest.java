package grafo.model.edge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeightedEdgeTest {

    @Test
    public void GetWeighttest() {
        WeightedEdge peso = new WeightedEdge(7.5);
        assertEquals(7.5, peso.getWeight());
    }
}
