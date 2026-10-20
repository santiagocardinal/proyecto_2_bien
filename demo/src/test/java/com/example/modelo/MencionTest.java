package com.example.modelo;

import org.junit.Test;

import static org.junit.Assert.*;

public class MencionTest {

    @Test
    public void crearMencionTest() {
        Usuario ana = new Usuario("ana");
        Usuario pedro = new Usuario("pedro");
        Posteo posteo = new Posteo("P1", "Hola @pedro", ana);

        Mencion mencion = new Mencion(posteo, pedro);

        assertEquals(posteo, mencion.getPosteo());
        assertEquals(pedro, mencion.getUsuarioMencionado());
    }

    @Test(expected = NullPointerException.class)
    public void mencionSinPosteoTest() {
        new Mencion(null, new Usuario("pedro"));
    }

    @Test(expected = NullPointerException.class)
    public void mencionSinUsuarioTest() {
        Posteo posteo = new Posteo("P1", "Hola mundo", new Usuario("ana"));

        new Mencion(posteo, null);
    }

    @Test
    public void mencionesIgualesTest() {
        Usuario ana = new Usuario("ana");
        Usuario pedro = new Usuario("pedro");
        Posteo posteo = new Posteo("P1", "Hola @pedro", ana);

        Mencion m1 = new Mencion(posteo, pedro);
        Mencion m2 = new Mencion(posteo, pedro);

        assertEquals(m1, m2);
    }

    @Test
    public void mencionesAUsuariosDistintosNoSonIgualesTest() {
        Usuario ana = new Usuario("ana");
        Usuario pedro = new Usuario("pedro");
        Usuario maria = new Usuario("maria");
        Posteo posteo = new Posteo("P1", "Hola @pedro y @maria", ana);

        Mencion m1 = new Mencion(posteo, pedro);
        Mencion m2 = new Mencion(posteo, maria);

        assertNotEquals(m1, m2);
    }

    @Test
    public void mencionesEnPosteosDistintosNoSonIgualesTest() {
        Usuario ana = new Usuario("ana");
        Usuario pedro = new Usuario("pedro");
        Posteo p1 = new Posteo("P1", "Hola @pedro", ana);
        Posteo p2 = new Posteo("P2", "Chau @pedro", ana);

        Mencion m1 = new Mencion(p1, pedro);
        Mencion m2 = new Mencion(p2, pedro);

        assertNotEquals(m1, m2);
    }
}