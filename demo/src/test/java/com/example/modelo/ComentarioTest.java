package com.example.modelo;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ComentarioTest {

    @Test
    public void crearComentarioTest() {
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Posteo posteo = new Posteo("P1", "Hola mundo", ana);
        LocalDateTime fecha = LocalDateTime.now();

        Comentario comentario = new Comentario("C1", "Muy buen posteo", bruno, posteo, fecha);

        assertEquals("C1", comentario.getId());
        assertEquals("Muy buen posteo", comentario.getContenido());
        assertEquals(bruno, comentario.getAutor());
        assertEquals(posteo, comentario.getPosteo());
        assertEquals(fecha, comentario.getFecha());
    }

    @Test
    public void crearComentarioSinFechaTest() {
        Usuario ana = new Usuario("ana");
        Posteo posteo = new Posteo("P1", "Hola mundo", ana);

        Comentario comentario = new Comentario("C1", "Muy buen posteo", ana, posteo);

        assertNotNull(comentario.getFecha());
    }

    @Test(expected = NullPointerException.class)
    public void comentarioSinAutorTest() {
        Posteo posteo = new Posteo("P1", "Hola mundo", new Usuario("ana"));

        new Comentario("C1", "Muy buen posteo", null, posteo);
    }

    @Test(expected = NullPointerException.class)
    public void comentarioSinPosteoTest() {
        new Comentario("C1", "Muy buen posteo", new Usuario("ana"), null);
    }

    @Test
    public void comentariosConMismoIdSonIgualesTest() {
        Usuario ana = new Usuario("ana");
        Posteo posteo = new Posteo("P1", "Hola mundo", ana);

        Comentario c1 = new Comentario("C1", "Hola", ana, posteo);
        Comentario c2 = new Comentario("C1", "Chau", ana, posteo);

        assertEquals(c1, c2);
    }

    @Test
    public void comentariosConDistintoIdNoSonIgualesTest() {
        Usuario ana = new Usuario("ana");
        Posteo posteo = new Posteo("P1", "Hola mundo", ana);

        Comentario c1 = new Comentario("C1", "Hola", ana, posteo);
        Comentario c2 = new Comentario("C2", "Hola", ana, posteo);

        assertNotEquals(c1, c2);
    }
}