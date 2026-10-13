package grafo;

import grafo.model.edge.Edge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph<String, Integer> grafo;

    @BeforeEach
    public void setUp() {
        grafo = new Graph<>();
        grafo.agregarVertice("A");
        grafo.agregarVertice("B");
        grafo.agregarVertice("C");
    }

    @Test
    public void AgregarVerticetest() {
        assertTrue(grafo.agregarVertice("D"));
        assertEquals(4, grafo.cantidadDeVertices());
    }

    @Test
    public void AgregarVerticeRepetidotest() {
        // A ya esta, no lo tendria que agregar de nuevo
        assertFalse(grafo.agregarVertice("A"));
        assertEquals(3, grafo.cantidadDeVertices());
    }

    @Test
    public void BuscarVerticetest() {
        assertEquals("B", grafo.buscarVertice(grafo.construirComparable("B")));
        assertNull(grafo.buscarVertice(grafo.construirComparable("Z")));
    }

    @Test
    public void AgregarAristatest() {
        assertTrue(grafo.agregarArista("A", "B", 1));

        // al ser no dirigido tiene que existir en los dos sentidos
        assertTrue(grafo.existeArista(grafo.construirComparable("A"), grafo.construirComparable("B")));
        assertTrue(grafo.existeArista(grafo.construirComparable("B"), grafo.construirComparable("A")));
        assertEquals(1, grafo.cantidadDeAristas());
    }

    @Test
    public void AgregarAristaVerticeQueNoExistetest() {
        assertFalse(grafo.agregarArista("A", "Z", 1));
    }

    @Test
    public void EliminarAristatest() {
        grafo.agregarArista("A", "B", 1);
        assertTrue(grafo.eliminarArista(grafo.construirComparable("B"), grafo.construirComparable("A")));

        assertFalse(grafo.existeArista(grafo.construirComparable("A"), grafo.construirComparable("B")));
        assertEquals(0, grafo.cantidadDeAristas());
    }

    @Test
    public void RemoverVerticetest() {
        grafo.agregarArista("A", "B", 1);
        grafo.agregarArista("B", "C", 2);

        assertTrue(grafo.removerVertice(grafo.construirComparable("B")));
        // si saco B se tienen que ir las dos aristas
        assertEquals(2, grafo.cantidadDeVertices());
        assertEquals(0, grafo.cantidadDeAristas());
    }

    @Test
    public void Adyacenciastest() {
        grafo.agregarArista("A", "B", 1);
        grafo.agregarArista("C", "A", 2);

        List<Edge<String, Integer>> ady = grafo.adyacencias(grafo.construirComparable("A"));
        assertEquals(2, ady.size());
        for (Edge<String, Integer> e : ady) {
            // el source siempre tendria que ser A
            assertEquals("A", e.source());
        }
    }

    @Test
    public void Vaciartest() {
        grafo.vaciar();
        assertEquals(0, grafo.cantidadDeVertices());
    }
}
