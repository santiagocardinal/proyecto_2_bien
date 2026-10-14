package com.example.grafo;

import com.example.grafo.model.edge.Edge;
import com.example.grafo.model.edge.WeightedEdge;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UndirectedGraphAlgorithmImplTest {

    // la uso para tener un grafo no dirigido en los tests
    static class GrafoNoDirigido<V, D> extends Graph<V, D> implements IUndirectedGraph<V, D> {
    }

    private GrafoNoDirigido<String, WeightedEdge> grafo;
    private UndirectedGraphAlgorithmImpl algoritmos;

    @Before
    public void setUp() {
        grafo = new GrafoNoDirigido<>();
        algoritmos = new UndirectedGraphAlgorithmImpl();

        grafo.agregarVertice("1");
        grafo.agregarVertice("2");
        grafo.agregarVertice("3");
        grafo.agregarVertice("4");

        grafo.agregarArista("1", "2", new WeightedEdge(1));
        grafo.agregarArista("2", "3", new WeightedEdge(2));
        grafo.agregarArista("1", "3", new WeightedEdge(3));
        grafo.agregarArista("3", "4", new WeightedEdge(4));
    }

    private double costoTotal(IUndirectedGraph<String, WeightedEdge> g) {
        double total = 0;
        for (Edge<String, WeightedEdge> arista : g.aristas()) {
            total += arista.dato().getWeight();
        }
        return total;
    }

    @Test
    public void Kruskaltest() {
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.kruskal(grafo);

        // con 4 vertices el arbol tiene 3 aristas
        assertEquals(3, arbol.cantidadDeAristas());
        assertEquals(7.0, costoTotal(arbol), 0.001);
        assertFalse(arbol.tieneCiclos());
    }

    @Test
    public void Primtest() {
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.prim(grafo, grafo.construirComparable("1"));

        assertEquals(3, arbol.cantidadDeAristas());
        // tiene que dar lo mismo que kruskal
        assertEquals(7.0, costoTotal(arbol), 0.001);
    }

    @Test
    public void SearchMinEdgetest() {
        Edge<String, WeightedEdge> arista = algoritmos.searchMinEdge(grafo, List.of("3"), List.of("1", "2", "4"));
        assertEquals(2.0, arista.dato().getWeight(), 0.001);
    }

}
