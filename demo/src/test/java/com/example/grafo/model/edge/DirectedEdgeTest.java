package com.example.grafo.model.edge;

import org.junit.Test;

import static org.junit.Assert.*;

public class DirectedEdgeTest {

    @Test
    public void CrearAristatest() {
        DirectedEdge<String, Integer> arista = new DirectedEdge<>("A", "B", 5);

        assertEquals("A", arista.source());
        assertEquals("B", arista.target());
        assertEquals(5, (int) arista.dato());
    }

    @Test
    public void AristasIgualestest() {
        DirectedEdge<String, Integer> a1 = new DirectedEdge<>("A", "B", 5);
        DirectedEdge<String, Integer> a2 = new DirectedEdge<>("A", "B", 10);

        // el dato no importa para equals, solo origen y destino
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    public void AristaInvertidaNoEsIgualtest() {
        DirectedEdge<String, Integer> a1 = new DirectedEdge<>("A", "B", 5);
        DirectedEdge<String, Integer> a2 = new DirectedEdge<>("B", "A", 5);

        // como es dirigida A->B no es lo mismo que B->A
        assertNotEquals(a1, a2);
    }

    @Test(expected = NullPointerException.class)
    public void OrigenNulotest() {
        // con origen null tiene que tirar excepcion
        new DirectedEdge<>(null, "B", 1);
    }
}
