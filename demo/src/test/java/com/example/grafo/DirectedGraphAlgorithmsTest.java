package com.example.grafo;

import com.example.grafo.model.edge.WeightedEdge;
import com.example.grafo.model.result.IDijkstraResult;
import com.example.grafo.model.result.IFloydWarshallResult;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DirectedGraphAlgorithmsTest {

    private DirectedGraph<String, WeightedEdge> grafo;
    private DirectedGraphAlgorithms algoritmos;

    @Before
    public void setUp() {
        grafo = new DirectedGraph<>();
        algoritmos = new DirectedGraphAlgorithms();

        grafo.agregarVertice("A");
        grafo.agregarVertice("B");
        grafo.agregarVertice("C");
        grafo.agregarVertice("D");

        grafo.agregarArista("A", "B", new WeightedEdge(1));
        grafo.agregarArista("B", "C", new WeightedEdge(2));
        grafo.agregarArista("A", "C", new WeightedEdge(5));
        grafo.agregarArista("C", "D", new WeightedEdge(1));
    }

    @Test 
    
    public void Dijkstratest() {
        IDijkstraResult<String> res = algoritmos.dijkstra(grafo.construirComparable("A"), grafo);

        // A-B-C-D sale 4, mas barato que ir directo A-C
        assertEquals(4.0, res.getCost("D"), 0.001);
        assertEquals(List.of("A", "B", "C", "D"), res.getPath("D"));
    }

    @Test

    public void DijkstraNoAlcanzabletest() {
        IDijkstraResult<String> res = algoritmos.dijkstra(grafo.construirComparable("D"), grafo);
        // desde D no se llega a ningun lado
        assertEquals(Double.POSITIVE_INFINITY, res.getCost("A"), 0.001);
    }

    @Test
    public void Floydtest() {
        IFloydWarshallResult<String> res = algoritmos.floyd(grafo);

        assertEquals(3.0, res.getCost("A", "C"), 0.001);
        assertEquals(List.of("A", "B", "C"), res.getPath("A", "C"));
        assertEquals(0.0, res.getCost("B", "B"), 0.001);
    }

    @Test
    public void RecorridoEnProfundidadtest() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnProfundidad(grafo, grafo.construirComparable("A"), visitados::add);

        assertEquals(4, visitados.size());
        assertEquals("A", visitados.get(0));
    }

    @Test
    public void ClasificacionTopologicatest() {
        List<String> orden = algoritmos.calcularClasificacionTopologica(grafo);
        assertEquals(List.of("A", "B", "C", "D"), orden);
    }

}
