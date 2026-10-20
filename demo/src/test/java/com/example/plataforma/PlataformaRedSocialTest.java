package com.example.plataforma;

import org.junit.Before;
import org.junit.Test;

import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class PlataformaRedSocialTest {

    private PlataformaRedSocial red;
    private Usuario ana;
    private Usuario bruno;
    private Usuario carla;
    private Usuario diego;

    @Before
    public void setUp() {
        red = new PlataformaRedSocial();

        ana = new Usuario("ana");
        bruno = new Usuario("bruno");
        carla = new Usuario("carla");
        diego = new Usuario("diego");

        red.registrarUsuario(ana);
        red.registrarUsuario(bruno);
        red.registrarUsuario(carla);
        red.registrarUsuario(diego);
    }

    // ---------------------------------------------------------- Usuarios

    @Test
    public void registrarUsuarioDuplicadoTest() {
        assertFalse(red.registrarUsuario(new Usuario("ana")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void seguirUsuarioNoRegistradoTest() {
        Usuario desconocido = new Usuario("desconocido");

        red.seguirUsuario(desconocido, ana);
    }

    @Test(expected = IllegalArgumentException.class)
    public void publicarConUsuarioNoRegistradoTest() {
        Usuario desconocido = new Usuario("desconocido");

        red.publicar(desconocido, "Hola");
    }

    @Test
    public void seguirYDejarDeSeguirTest() {
        red.seguirUsuario(bruno, ana);
        assertTrue(red.obtenerSeguidores(ana).contains(bruno));

        red.dejarDeSeguirUsuario(bruno, ana);
        assertFalse(red.obtenerSeguidores(ana).contains(bruno));
    }

    // ----------------------------------------------------------- Alcance

    @Test
    public void alcanceLlegaALosSeguidoresTest() {
        red.seguirUsuario(bruno, ana);
        red.seguirUsuario(carla, ana);

        Posteo posteo = red.publicar(ana, "Hola");
        Set<Usuario> alcance = red.calcularAlcance(posteo);

        assertEquals(2, alcance.size());
        assertTrue(alcance.contains(bruno));
        assertTrue(alcance.contains(carla));
    }

    @Test
    public void alcanceNoIncluyeAlAutorTest() {
        red.seguirUsuario(bruno, ana);

        Posteo posteo = red.publicar(ana, "Hola");

        assertFalse(red.calcularAlcance(posteo).contains(ana));
    }

    @Test
    public void posteoSinSeguidoresNoLlegaANadieTest() {
        Posteo posteo = red.publicar(ana, "Nadie me sigue");

        assertTrue(red.calcularAlcance(posteo).isEmpty());
    }

    @Test
    public void alcanceIncluyeAlMencionadoTest() {
        // diego no sigue a ana, pero lo mencionan
        Posteo posteo = red.publicar(ana, "Hola @diego");

        assertTrue(red.calcularAlcance(posteo).contains(diego));
    }

    @Test
    public void alcanceIncluyeMencionEnComentarioTest() {
        red.seguirUsuario(bruno, ana);
        Posteo posteo = red.publicar(ana, "Hola");

        red.comentar(bruno, posteo, "@diego mira esto");

        assertTrue(red.calcularAlcance(posteo).contains(diego));
    }

    // -------------------------------------------------------- Compartir

    @Test
    public void mencionadoQueComparteLlegaASusSeguidoresTest() {
        // carla sigue a diego, no a ana
        red.seguirUsuario(carla, diego);

        Posteo posteo = red.publicar(ana, "Hola @diego");
        assertTrue(red.compartir(diego, posteo));

        // al compartir diego, el posteo le llega a carla
        assertTrue(red.calcularAlcance(posteo).contains(carla));
    }

    @Test
    public void noSePuedeCompartirSiNoLeLlegoTest() {
        red.seguirUsuario(carla, diego);

        // diego no sigue a ana ni fue mencionado
        Posteo posteo = red.publicar(ana, "Hola");

        assertFalse(red.compartir(diego, posteo));
        assertFalse(red.calcularAlcance(posteo).contains(carla));
    }

    // ------------------------------------------------------------- Feed

    @Test
    public void feedIncluyePosteoDeSeguidoTest() {
        red.seguirUsuario(bruno, ana);

        Posteo posteo = red.publicar(ana, "Hola");

        assertTrue(red.obtenerFeed(bruno).contains(posteo));
    }

    @Test
    public void feedIncluyePosteoCompartidoTest() {
        red.seguirUsuario(bruno, ana);
        red.seguirUsuario(carla, bruno);

        Posteo posteo = red.publicar(ana, "Hola");
        red.compartir(bruno, posteo);

        // carla no sigue a ana, pero sigue a bruno que lo compartio
        assertTrue(red.obtenerFeed(carla).contains(posteo));
    }

    @Test
    public void dejarDeSeguirSacaElPosteoDelFeedTest() {
        red.seguirUsuario(bruno, ana);
        Posteo posteo = red.publicar(ana, "Hola");
        assertTrue(red.obtenerFeed(bruno).contains(posteo));

        red.dejarDeSeguirUsuario(bruno, ana);

        assertFalse(red.obtenerFeed(bruno).contains(posteo));
    }

    // --------------------------------- Todos usan el mismo grafo

    @Test
    public void analisisVeLosSeguidoresDeLaPlataformaTest() {
        red.seguirUsuario(bruno, ana);
        red.seguirUsuario(carla, ana);

        Map<Usuario, Integer> influencia = red.calcularInfluencia();

        assertEquals(2, (int) influencia.get(ana));
    }

    @Test
    public void usuarioSinSeguidoresApareceEnElAnalisisTest() {
        Map<Usuario, Integer> influencia = red.calcularInfluencia();

        assertEquals(0, (int) influencia.get(diego));
    }

    @Test
    public void sugerenciasUsanLosSeguidoresDeLaPlataformaTest() {
        red.seguirUsuario(ana, bruno);
        red.seguirUsuario(bruno, carla);

        assertTrue(red.sugerirUsuarios(ana).contains(carla));
    }
}