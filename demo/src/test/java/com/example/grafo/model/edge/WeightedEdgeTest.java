package com.example.grafo.model.edge;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeightedEdgeTest {

    @Test
    public void GetWeighttest() {
        WeightedEdge peso = new WeightedEdge(7.5);
        assertEquals(7.5, peso.getWeight(), 0.001);
    }
}
