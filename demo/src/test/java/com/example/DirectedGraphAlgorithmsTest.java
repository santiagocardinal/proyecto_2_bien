package grafo;

import grafo.model.edge.WeightedEdge;
import grafo.model.result.IDijkstraResult;
import grafo.model.result.IFloydWarshallResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class DirectedGraphAlgorithmsTest {

    private DirectedGraph<String, WeightedEdge> grafo;
    private DirectedGraphAlgorithms algoritmos;

    @BeforeEach
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
        assertEquals(4.0, res.getCost("D"));
        assertEquals(List.of("A", "B", "C", "D"), res.getPath("D"));
    }

    @Test
    public void DijkstraNoAlcanzabletest() {
        IDijkstraResult<String> res = algoritmos.dijkstra(grafo.construirComparable("D"), grafo);
        // desde D no se llega a ningun lado
        assertEquals(Double.POSITIVE_INFINITY, res.getCost("A"));
    }

    @Test
    public void Floydtest() {
        IFloydWarshallResult<String> res = algoritmos.floyd(grafo);

        assertEquals(3.0, res.getCost("A", "C"));
        assertEquals(List.of("A", "B", "C"), res.getPath("A", "C"));
        assertEquals(0.0, res.getCost("B", "B"));
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
