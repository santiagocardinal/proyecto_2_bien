package grafo.model.result;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class DijkstraResultTest {

    @Test
    public void CostoYCaminotest() {
        Map<String, Double> costos = new HashMap<>();
        costos.put("A", 0.0);
        costos.put("B", 4.0);

        Map<String, List<String>> caminos = new HashMap<>();
        caminos.put("B", List.of("A", "B"));

        DijkstraResult<String> resultado = new DijkstraResult<>(costos, caminos);

        assertEquals(4.0, resultado.getCost("B"));
        assertEquals(List.of("A", "B"), resultado.getPath("B"));
    }

    @Test
    public void VerticeQueNoEstatest() {
        DijkstraResult<String> resultado = new DijkstraResult<>(new HashMap<>(), new HashMap<>());

        // si no esta el vertice tiene que dar infinito y camino vacio
        assertEquals(Double.POSITIVE_INFINITY, resultado.getCost("Z"));
        assertTrue(resultado.getPath("Z").isEmpty());
    }
}
