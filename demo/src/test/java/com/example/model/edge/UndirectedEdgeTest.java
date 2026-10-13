package grafo.model.edge;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

public class UndirectedEdgeTest {

    @Test
    public void CrearAristatest() {
        UndirectedEdge<String, Integer> arista = new UndirectedEdge<>("A", "B", 3);

        assertEquals("A", arista.source());
        assertEquals("B", arista.target());
        assertEquals(3, arista.dato());
        assertFalse(arista.directed());
    }

    @Test
    public void EqualsEnAmbosSentidostest() {
        UndirectedEdge<String, Integer> a1 = new UndirectedEdge<>("A", "B", 3);
        UndirectedEdge<String, Integer> a2 = new UndirectedEdge<>("B", "A", 3);

        // aca quiero que {A,B} sea igual a {B,A}
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    public void AristasDistintastest() {
        UndirectedEdge<String, Integer> a1 = new UndirectedEdge<>("A", "B", 3);
        UndirectedEdge<String, Integer> a2 = new UndirectedEdge<>("A", "C", 3);

        assertNotEquals(a1, a2);
    }
}
