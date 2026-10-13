package grafo;

import grafo.model.edge.WeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

public class UndirectedGraphAlgorithmImplTest {

    // la uso para tener un grafo no dirigido en los tests
    static class GrafoNoDirigido<V, D> extends Graph<V, D> implements IUndirectedGraph<V, D> {
    }

    private GrafoNoDirigido<String, WeightedEdge> grafo;
    private UndirectedGraphAlgorithmImpl algoritmos;

    @BeforeEach
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
        for (var arista : g.aristas()) {
            total += arista.dato().getWeight();
        }
        return total;
    }

    @Test
    public void Kruskaltest() {
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.kruskal(grafo);

        // con 4 vertices el arbol tiene 3 aristas
        assertEquals(3, arbol.cantidadDeAristas());
        assertEquals(7.0, costoTotal(arbol));
        assertFalse(arbol.tieneCiclos());
    }

    @Test
    public void Primtest() {
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.prim(grafo, grafo.construirComparable("1"));

        assertEquals(3, arbol.cantidadDeAristas());
        // tiene que dar lo mismo que kruskal
        assertEquals(7.0, costoTotal(arbol));
    }

    @Test
    public void SearchMinEdgetest() {
        var arista = algoritmos.searchMinEdge(grafo, List.of("3"), List.of("1", "2", "4"));
        assertEquals(2.0, arista.dato().getWeight());
    }

}
