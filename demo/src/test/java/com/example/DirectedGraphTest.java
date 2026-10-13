package grafo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class DirectedGraphTest {

    private DirectedGraph<String, Integer> grafo;

    @BeforeEach
    public void setUp() {
        grafo = new DirectedGraph<>();
        grafo.agregarVertice("A");
        grafo.agregarVertice("B");
        grafo.agregarVertice("C");
    }

    @Test
    public void AgregarVerticetest() {
        assertTrue(grafo.agregarVertice("D"));
        assertFalse(grafo.agregarVertice("D"));
        assertFalse(grafo.agregarVertice(null));
    }

    @Test
    public void AgregarAristaEsDirigidatest() {
        grafo.agregarArista("A", "B", 3);

        assertTrue(grafo.existeArista(grafo.construirComparable("A"), grafo.construirComparable("B")));
        // al reves no deberia existir
        assertFalse(grafo.existeArista(grafo.construirComparable("B"), grafo.construirComparable("A")));
    }

    @Test
    public void ObtenerAristatest() {
        grafo.agregarArista("A", "C", 8);
        assertEquals(8, grafo.obtenerArista("A", "C").dato());
    }

    @Test
    public void EliminarAristatest() {
        grafo.agregarArista("A", "B", 3);
        assertTrue(grafo.eliminarArista(grafo.construirComparable("A"), grafo.construirComparable("B")));
        // la segunda vez ya no esta
        assertFalse(grafo.eliminarArista(grafo.construirComparable("A"), grafo.construirComparable("B")));
    }

    @Test
    public void RemoverVerticetest() {
        grafo.agregarArista("A", "B", 1);
        grafo.agregarArista("C", "B", 1);

        assertTrue(grafo.removerVertice(grafo.construirComparable("B")));
        assertEquals(2, grafo.cantidadDeVertices());
        assertEquals(0, grafo.cantidadDeAristas());
    }

    @Test
    public void SucesoresYPredecesorestest() {
        grafo.agregarArista("A", "B", 1);
        grafo.agregarArista("A", "C", 1);
        grafo.agregarArista("C", "B", 1);

        Set<String> sucesores = grafo.successors(grafo.construirComparable("A"));
        assertEquals(Set.of("B", "C"), sucesores);

        Set<String> predecesores = grafo.predecessors(grafo.construirComparable("B"));
        assertEquals(Set.of("A", "C"), predecesores);

        assertEquals(2, grafo.gradoDeSalida(grafo.construirComparable("A")));
        assertEquals(2, grafo.gradoDeEntrada(grafo.construirComparable("B")));
    }

    @Test
    public void TieneCiclostest() {
        grafo.agregarArista("A", "B", 1);
        grafo.agregarArista("B", "C", 1);
        assertFalse(grafo.tieneCiclos());

        grafo.agregarArista("C", "A", 1);
        assertTrue(grafo.tieneCiclos());
    }
}
