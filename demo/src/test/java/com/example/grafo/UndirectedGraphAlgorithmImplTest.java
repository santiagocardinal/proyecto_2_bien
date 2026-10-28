package com.example.grafo;
import java.util.ArrayList;
import java.util.Set;
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
  
    // Crea un grafo no dirigido nuevo con los vertices indicados
    private GrafoNoDirigido<String, WeightedEdge> nuevoGrafo(String... vertices) {
        GrafoNoDirigido<String, WeightedEdge> g = new GrafoNoDirigido<>();
        for (String v : vertices) {
            g.agregarVertice(v);
        }
        return g;
    }

    @Test
    public void kruskalGrafoNoConexoDevuelveBosqueTest() {
        // dos componentes: {A, B} y {C, D}
        GrafoNoDirigido<String, WeightedEdge> g = nuevoGrafo("A", "B", "C", "D");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("C", "D", new WeightedEdge(2));

        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.kruskal(g);

        assertEquals(4, arbol.cantidadDeVertices());
        assertEquals(2, arbol.cantidadDeAristas()); // una arista por componente
    }

    @Test
    public void primGrafoNoConexoSoloCubreSuComponenteTest() {
        GrafoNoDirigido<String, WeightedEdge> g = nuevoGrafo("A", "B", "C", "D");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("C", "D", new WeightedEdge(2));

        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.prim(g, g.construirComparable("A"));

        assertEquals(1, arbol.cantidadDeAristas()); // solo la arista A-B
    }

    @Test
    public void kruskalYPrimUnVerticeTest() {
        GrafoNoDirigido<String, WeightedEdge> g = nuevoGrafo("A");

        assertEquals(0, algoritmos.kruskal(g).cantidadDeAristas());
        assertEquals(0, algoritmos.prim(g, g.construirComparable("A")).cantidadDeAristas());
    }

    @Test
    public void kruskalConPesosIgualesTest() {
        // triangulo con todas las aristas de peso 1: el arbol usa 2 de las 3
        GrafoNoDirigido<String, WeightedEdge> g = nuevoGrafo("A", "B", "C");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("B", "C", new WeightedEdge(1));
        g.agregarArista("C", "A", new WeightedEdge(1));

        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.kruskal(g);

        assertEquals(2, arbol.cantidadDeAristas());
        assertFalse(arbol.tieneCiclos());
        assertTrue(arbol.esConexo());
    }

    @Test
    public void primOrigenInexistenteTest() {
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.prim(grafo, grafo.construirComparable("Z"));

        assertEquals(0, arbol.cantidadDeAristas());
    }

    @Test
    public void beaRecorreTodosLosVerticesTest() {
        List<String> visitados = new ArrayList<>();
        algoritmos.bea(grafo, visitados::add);

        assertEquals(Set.of("1", "2", "3", "4"), Set.copyOf(visitados));
        assertEquals(4, visitados.size()); // ninguno repetido
    }

    @Test
    public void beaNoConexoRecorreTodasLasComponentesTest() {
        GrafoNoDirigido<String, WeightedEdge> g = nuevoGrafo("A", "B", "C", "D");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("C", "D", new WeightedEdge(1));

        List<String> visitados = new ArrayList<>();
        algoritmos.bea(g, visitados::add);

        // visita a los 4 aunque esten en dos componentes distintas
        assertEquals(Set.of("A", "B", "C", "D"), Set.copyOf(visitados));
    }

    @Test
    public void beaGrafoVacioTest() {
        List<String> visitados = new ArrayList<>();
        algoritmos.bea(nuevoGrafo(), visitados::add);

        assertTrue(visitados.isEmpty());
    }
}
