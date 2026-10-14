package com.example.grafo.model.result;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FloydWarshallResultTest {

    @Test
    public void Resultadotest() {
        Map<String, Map<String, Double>> costos = new HashMap<>();
        costos.put("A", new HashMap<>());
        costos.get("A").put("B", 2.0);

        Map<String, Map<String, List<String>>> caminos = new HashMap<>();
        caminos.put("A", new HashMap<>());
        caminos.get("A").put("B", List.of("A", "B"));

        FloydWarshallResult<String> resultado = new FloydWarshallResult<>(costos, caminos);

        assertEquals(2.0, resultado.getCost("A", "B"), 0.001);
        assertEquals(List.of("A", "B"), resultado.getPath("A", "B"));
        assertTrue(resultado.connected("A", "B"));
    }

    @Test
    public void NoConectadostest() {
        FloydWarshallResult<String> resultado = new FloydWarshallResult<>(new HashMap<>(), new HashMap<>());

        // no hay nada cargado asi que no deberian estar conectados
        assertFalse(resultado.connected("A", "B"));
        assertTrue(resultado.getPath("A", "B").isEmpty());
    }
}
