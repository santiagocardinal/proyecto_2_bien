package com.example.grafo;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class DirectedGraphTest {

    private DirectedGraph<String, Integer> grafo;

    @Before
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
        assertEquals(8, (int) grafo.obtenerArista("A", "C").dato());
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
     
    @Test
    public void grafoVacioTest() {
        DirectedGraph<String, Integer> vacio = new DirectedGraph<>();

        assertEquals(0, vacio.cantidadDeVertices());
        assertEquals(0, vacio.cantidadDeAristas());
        assertFalse(vacio.tieneCiclos());
        assertTrue(vacio.esConexo());
    }

    @Test
    public void unVerticeTest() {
        DirectedGraph<String, Integer> uno = new DirectedGraph<>();
        uno.agregarVertice("A");

        assertTrue(uno.esConexo());
        assertFalse(uno.tieneCiclos());
        assertTrue(uno.successors(uno.construirComparable("A")).isEmpty());
        assertTrue(uno.predecessors(uno.construirComparable("A")).isEmpty());
    }

    @Test
    public void conexoYNoConexoTest() {
        grafo.agregarArista("A", "B", 1);
        assertFalse(grafo.esConexo()); // C queda aislado

        grafo.agregarArista("C", "B", 1);
        assertTrue(grafo.esConexo()); // ahora todos estan unidos (sin mirar la direccion)
    }

    @Test
    public void verticeInexistenteTest() {
        assertNull(grafo.buscarVertice(grafo.construirComparable("Z")));
        assertTrue(grafo.successors(grafo.construirComparable("Z")).isEmpty());
        assertTrue(grafo.predecessors(grafo.construirComparable("Z")).isEmpty());
        assertTrue(grafo.adyacencias(grafo.construirComparable("Z")).isEmpty());
        assertFalse(grafo.agregarArista("A", "Z", 1));
        assertFalse(grafo.eliminarArista(grafo.construirComparable("A"), grafo.construirComparable("Z")));
        assertFalse(grafo.removerVertice(grafo.construirComparable("Z")));
    }

    @Test
    public void lazoEsCicloTest() {
        grafo.agregarArista("A", "A", 1);

        assertTrue(grafo.tieneCiclos());
    }
}
