package com.example.modelo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PosteoTest {

    private Usuario ana;
    private Posteo posteo;

    @Before
    public void setUp() {
        ana = new Usuario("ana");
        posteo = new Posteo("P1", "Mi primer posteo", ana);
    }

    @Test
    public void crearPosteoTest() {
        assertEquals("P1", posteo.getId());
        assertEquals("Mi primer posteo", posteo.getContenido());
        assertEquals(ana, posteo.getAutor());
        assertTrue(posteo.getComentarios().isEmpty());
        assertTrue(posteo.getMenciones().isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void posteoSinAutorTest() {
        new Posteo("P2", "Sin autor", null);
    }


    @Test
    public void agregarComentarioTest() {
        Comentario comentario = new Comentario("C1", "Buen posteo", new Usuario("bruno"), posteo);

        posteo.agregarComentario(comentario);

        assertEquals(1, posteo.getComentarios().size());
        assertSame(comentario, posteo.getComentarios().get(0));
    }

    @Test
    public void agregarVariosComentariosTest() {
        Comentario c1 = new Comentario("C1", "Hola", new Usuario("bruno"), posteo);
        Comentario c2 = new Comentario("C2", "Muy bueno", new Usuario("carla"), posteo);

        posteo.agregarComentario(c1);
        posteo.agregarComentario(c2);

        assertEquals(2, posteo.getComentarios().size());
    }

    @Test
    public void agregarComentarioNullTest() {
        posteo.agregarComentario(null);

        assertTrue(posteo.getComentarios().isEmpty());
    }


    @Test
    public void agregarMencionTest() {
        Usuario juan = new Usuario("juan");

        posteo.agregarMencion(juan);

        assertEquals(1, posteo.getMenciones().size());

        Mencion mencion = posteo.getMenciones().get(0);

        assertSame(posteo, mencion.getPosteo());
        assertSame(juan, mencion.getUsuarioMencionado());
    }

    @Test
    public void agregarVariasMencionesTest() {
        posteo.agregarMencion(new Usuario("juan"));
        posteo.agregarMencion(new Usuario("pedro"));

        assertEquals(2, posteo.getMenciones().size());
    }

    @Test
    public void agregarMencionNullTest() {
        posteo.agregarMencion(null);

        assertTrue(posteo.getMenciones().isEmpty());
    }

    @Test
    public void noRepetirMencionTest() {
        Usuario juan = new Usuario("juan");

        posteo.agregarMencion(juan);
        posteo.agregarMencion(juan);

        assertEquals(1, posteo.getMenciones().size());
    }

    @Test
    public void fueMencionadoTest() {
        Usuario juan = new Usuario("juan");
        Usuario pedro = new Usuario("pedro");

        posteo.agregarMencion(juan);

        assertTrue(posteo.fueMencionado(juan));
        assertFalse(posteo.fueMencionado(pedro));
    }

    // ------------------------------------------------------------------ Otros

    @Test
    public void noModificarListaOriginalTest() {
        Comentario comentario = new Comentario("C1", "Hola", new Usuario("bruno"), posteo);

        posteo.agregarComentario(comentario);
        posteo.agregarMencion(new Usuario("juan"));

        posteo.getComentarios().clear();
        posteo.getMenciones().clear();

        assertEquals(1, posteo.getComentarios().size());
        assertEquals(1, posteo.getMenciones().size());
    }

    @Test
    public void posteosConMismoIdSonIgualesTest() {
        Posteo otro = new Posteo("P1", "Otro texto", new Usuario("bruno"));

        assertEquals(posteo, otro);
    }

    @Test
    public void posteosConDistintoIdNoSonIgualesTest() {
        Posteo otro = new Posteo("P2", "Mi primer posteo", ana);

        assertNotEquals(posteo, otro);
    }
}