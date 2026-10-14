package com.example.grafo.model.result;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PathTest {

    @Test
    public void Pathtest() {
        Path<String> camino = new Path<>(List.of("A", "B", "C"), 10.0);

        assertEquals(3, camino.getPath().size());
        assertEquals(10.0, camino.getCost(), 0.001);
    }
}
