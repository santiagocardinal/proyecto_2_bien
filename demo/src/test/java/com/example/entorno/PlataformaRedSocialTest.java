
package com.example.entorno;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlataformaRedSocialTest {

    private PlataformaRedSocial plataforma;

    @Before
    public void setUp() {
        plataforma = new PlataformaRedSocial();
    }

    @Test
    public void registrarPosteoTest() {
        Posteo posteo = new Posteo("P1", "Hola mundo");

        assertTrue(plataforma.registrarPosteo(posteo));
        assertSame(posteo, plataforma.obtenerPosteo("P1"));
    }

    @Test
    public void registrarPosteoDuplicadoTest() {
        Posteo p1 = new Posteo("P1", "Primer posteo");
        Posteo p2 = new Posteo("P1", "Segundo posteo");

        assertTrue(plataforma.registrarPosteo(p1));
        assertFalse(plataforma.registrarPosteo(p2));

        assertSame(p1, plataforma.obtenerPosteo("P1"));
    }

    @Test
    public void registrarPosteoNullTest() {
        assertFalse(plataforma.registrarPosteo(null));
    }

    @Test
    public void registrarPosteoSinIdTest() {
        Posteo posteo = new Posteo(null, "Sin identificador");

        assertFalse(plataforma.registrarPosteo(posteo));
    }

    @Test
    public void obtenerPosteoInexistenteTest() {
        assertNull(plataforma.obtenerPosteo("P99"));
    }

    @Test
    public void registrarVariosPosteosTest() {
        Posteo p1 = new Posteo("P1", "Hola");
        Posteo p2 = new Posteo("P2", "Buen dia");

        assertTrue(plataforma.registrarPosteo(p1));
        assertTrue(plataforma.registrarPosteo(p2));

        assertSame(p1, plataforma.obtenerPosteo("P1"));
        assertSame(p2, plataforma.obtenerPosteo("P2"));
    }
}
