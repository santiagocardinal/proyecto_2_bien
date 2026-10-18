
package com.example.entorno;

import org.junit.Test;

import static org.junit.Assert.*;

public class MencionesTest {

    @Test
    public void crearMencionTest() {
        Posteo posteo = new Posteo("P1", "Hola mundo");
        Usuario usuario = new Usuario("Pedro");

        Menciones mencion = new Menciones(posteo, usuario);

        assertSame(posteo, mencion.getPosteo());
        assertSame(usuario, mencion.getUsuarioMencionado());
    }
}
