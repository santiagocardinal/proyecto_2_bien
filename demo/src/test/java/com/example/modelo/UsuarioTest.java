package com.example.modelo;

import org.junit.Test;

import static org.junit.Assert.*;

public class UsuarioTest {

    @Test
    public void crearUsuarioTest() {
        Usuario usuario = new Usuario("ana");

        assertEquals("ana", usuario.getNombreUsuario());
        assertEquals(0, usuario.getCantidadPost());
        assertEquals(0, usuario.getCantidadSeguidores());
        assertEquals(0, usuario.getCantidadSeguidos());
    }

    @Test
    public void cambiarNombreUsuarioTest() {
        Usuario usuario = new Usuario("ana");

        usuario.setNombreUsuario("bruno");

        assertEquals("bruno", usuario.getNombreUsuario());
    }

    @Test
    public void cambiarCantidadPostTest() {
        Usuario usuario = new Usuario("ana");

        usuario.setCantidadPost(5);

        assertEquals(5, usuario.getCantidadPost());
    }

    @Test
    public void cambiarCantidadSeguidoresTest() {
        Usuario usuario = new Usuario("ana");

        usuario.setCantidadSeguidores(10);

        assertEquals(10, usuario.getCantidadSeguidores());
    }

    @Test
    public void cambiarCantidadSeguidosTest() {
        Usuario usuario = new Usuario("ana");

        usuario.setCantidadSeguidos(7);

        assertEquals(7, usuario.getCantidadSeguidos());
    }

    @Test
    public void usuariosConMismoNombreSonIgualesTest() {
        Usuario usuario1 = new Usuario("ana");
        Usuario usuario2 = new Usuario("ana");

        assertEquals(usuario1, usuario2);
    }

    @Test
    public void usuariosConDistintoNombreNoSonIgualesTest() {
        Usuario usuario1 = new Usuario("ana");
        Usuario usuario2 = new Usuario("bruno");

        assertNotEquals(usuario1, usuario2);
    }

    @Test
    public void usuarioNoEsIgualANullTest() {
        Usuario usuario = new Usuario("ana");

        assertNotEquals(usuario, null);
    }

    @Test
    public void usuarioNoEsIgualAOtroTipoDeObjetoTest() {
        Usuario usuario = new Usuario("ana");

        assertNotEquals(usuario, "ana");
    }

    @Test
    public void usuariosIgualesTienenMismoHashCodeTest() {
        Usuario usuario1 = new Usuario("ana");
        Usuario usuario2 = new Usuario("ana");

        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }

    @Test
    public void compararUsuariosPorNombreTest() {
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        assertTrue(ana.compareTo(bruno) < 0);
        assertTrue(bruno.compareTo(ana) > 0);
    }

    @Test
    public void compararUsuariosConMismoNombreTest() {
        Usuario usuario1 = new Usuario("ana");
        Usuario usuario2 = new Usuario("ana");

        assertEquals(0, usuario1.compareTo(usuario2));
    }

    @Test
    public void compararUsuarioConNullTest() {
        Usuario usuario = new Usuario("ana");

        assertTrue(usuario.compareTo(null) > 0);
    }

    @Test
    public void mostrarUsuarioComoTextoTest() {
        Usuario usuario = new Usuario("ana");

        assertEquals("@ana", usuario.toString());
    }
}