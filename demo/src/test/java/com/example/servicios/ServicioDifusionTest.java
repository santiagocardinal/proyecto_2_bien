package com.example.servicios;

import org.junit.Before;
import org.junit.Test;

import com.example.grafo.DirectedGraph;
import com.example.grafo.model.edge.WeightedEdge;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

public class ServicioDifusionTest {

    private ServicioSeguidores seguidores;
    private ServicioDifusion difusion;

    private Usuario ana;
    private Usuario bruno;
    private Usuario carla;
    private Usuario diego;

    @Before
    public void setUp() {
        seguidores = new ServicioSeguidores();
        difusion = new ServicioDifusion(seguidores);

        ana = new Usuario("ana");
        bruno = new Usuario("bruno");
        carla = new Usuario("carla");
        diego = new Usuario("diego");
    }

    // ------------------------------------------------ Grafo de difusión

    @Test
    public void grafoTieneAristaDelAutorASuSeguidorTest() {
        seguidores.seguir(bruno, ana);
        Posteo posteo = new Posteo("P1", "Hola", ana);

        DirectedGraph<Usuario, WeightedEdge> grafo = difusion.construirGrafoDifusion(posteo);

        // la arista va de ana (quien publica) a bruno (a quien le llega)
        assertTrue(grafo.existeArista(ana, bruno));
        assertFalse(grafo.existeArista(bruno, ana));
    }

    @Test
    public void grafoTieneAristaHaciaElMencionadoTest() {
        Posteo posteo = new Posteo("P1", "Hola @diego", ana);
        posteo.agregarMencion(diego);

        DirectedGraph<Usuario, WeightedEdge> grafo = difusion.construirGrafoDifusion(posteo);

        assertTrue(grafo.existeArista(ana, diego));
    }

    @Test
    public void grafoTieneAristaDelCompartidorASusSeguidoresTest() {
        seguidores.seguir(carla, diego);
        Posteo posteo = new Posteo("P1", "Hola", ana);

        difusion.registrarCompartido(diego, posteo);
        DirectedGraph<Usuario, WeightedEdge> grafo = difusion.construirGrafoDifusion(posteo);

        assertTrue(grafo.existeArista(diego, carla));
    }

    @Test
    public void grafoDePosteoSinInteraccionesSoloTieneAlAutorTest() {
        Posteo posteo = new Posteo("P1", "Hola", ana);

        DirectedGraph<Usuario, WeightedEdge> grafo = difusion.construirGrafoDifusion(posteo);

        assertEquals(1, grafo.cantidadDeVertices());
        assertEquals(0, grafo.cantidadDeAristas());
    }

    // ----------------------------------------------------------- Alcance

    @Test
    public void alcanceDePosteoNullEsVacioTest() {
        assertTrue(difusion.calcularAlcance(null).isEmpty());
    }

    @Test
    public void alcanceEnCadenaPorCompartidoTest() {
        // ana <- bruno <- carla   (bruno sigue a ana, carla sigue a bruno)
        seguidores.seguir(bruno, ana);
        seguidores.seguir(carla, bruno);
        Posteo posteo = new Posteo("P1", "Hola", ana);

        difusion.registrarCompartido(bruno, posteo);
        Set<Usuario> alcance = difusion.calcularAlcance(posteo);

        assertEquals(2, alcance.size());
        assertTrue(alcance.contains(bruno));
        assertTrue(alcance.contains(carla));
    }

    @Test
    public void compartidorAlQueNoLeLlegoNoDifundeTest() {
        // diego comparte aunque el posteo nunca le llegó
        seguidores.seguir(carla, diego);
        Posteo posteo = new Posteo("P1", "Hola", ana);

        difusion.registrarCompartido(diego, posteo);

        // el BFS desde ana nunca llega a diego, así que tampoco a carla
        assertFalse(difusion.calcularAlcance(posteo).contains(carla));
    }

    @Test
    public void leLlegaTest() {
        seguidores.seguir(bruno, ana);
        Posteo posteo = new Posteo("P1", "Hola", ana);

        assertTrue(difusion.leLlega(ana, posteo));   // el autor
        assertTrue(difusion.leLlega(bruno, posteo)); // un seguidor
        assertFalse(difusion.leLlega(diego, posteo)); // alguien sin relación
    }

    // ------------------------------------------------------- Compartidos

    @Test
    public void posteoSinCompartidosTest() {
        Posteo posteo = new Posteo("P1", "Hola", ana);

        assertTrue(difusion.obtenerCompartidores(posteo).isEmpty());
    }

    @Test
    public void compartirDosVecesNoSeRepiteTest() {
        Posteo posteo = new Posteo("P1", "Hola", ana);

        difusion.registrarCompartido(bruno, posteo);
        difusion.registrarCompartido(bruno, posteo);

        assertEquals(1, difusion.obtenerCompartidores(posteo).size());
    }

    @Test
    public void fueCompartidoPorAlgunoTest() {
        Posteo posteo = new Posteo("P1", "Hola", ana);
        difusion.registrarCompartido(bruno, posteo);

        assertTrue(difusion.fueCompartidoPorAlguno(posteo, Arrays.asList(bruno, carla)));
        assertFalse(difusion.fueCompartidoPorAlguno(posteo, Arrays.asList(carla, diego)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void registrarCompartidoConUsuarioNullTest() {
        Posteo posteo = new Posteo("P1", "Hola", ana);

        difusion.registrarCompartido(null, posteo);
    }
}