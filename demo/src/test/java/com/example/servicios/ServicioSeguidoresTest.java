package com.example.servicios;

import com.example.modelo.Usuario;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ServicioSeguidoresTest {

    @Test
    public void crearServicioSeguidoresTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        assertNotNull(servicio.getGrafoSeguidores());
        assertTrue(servicio.getGrafoSeguidores().vertices().isEmpty());
    }

    @Test
    public void seguirUsuarioTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicio.seguir(ana, bruno);

        assertTrue(servicio.getGrafoSeguidores().existeArista(ana, bruno));
        assertEquals(1, ana.getCantidadSeguidos());
        assertEquals(1, bruno.getCantidadSeguidores());
    }

    @Test
    public void usuarioNoPuedeSeguirseASiMismoTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");

        servicio.seguir(ana, ana);

        assertEquals(0, ana.getCantidadSeguidos());
        assertEquals(0, ana.getCantidadSeguidores());
        assertTrue(servicio.obtenerSeguidos(ana).isEmpty());
        assertTrue(servicio.obtenerSeguidores(ana).isEmpty());
    }

    @Test
    public void dejarDeSeguirUsuarioTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicio.seguir(ana, bruno);
        servicio.dejarDeSeguir(ana, bruno);

        assertFalse(servicio.getGrafoSeguidores().existeArista(ana, bruno));
        assertEquals(0, ana.getCantidadSeguidos());
        assertEquals(0, bruno.getCantidadSeguidores());
    }

    @Test
    public void dejarDeSeguirSinSeguimientoPrevioNoModificaContadoresTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicio.dejarDeSeguir(ana, bruno);

        assertEquals(0, ana.getCantidadSeguidos());
        assertEquals(0, bruno.getCantidadSeguidores());
    }

    @Test(expected = IllegalArgumentException.class)
    public void dejarDeSeguirConPrimerUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.dejarDeSeguir(null, new Usuario("bruno"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dejarDeSeguirConSegundoUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.dejarDeSeguir(new Usuario("ana"), null);
    }

    @Test
    public void obtenerSeguidoresTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicio.seguir(bruno, ana);
        servicio.seguir(carla, ana);

        List<Usuario> seguidores = servicio.obtenerSeguidores(ana);

        assertEquals(2, seguidores.size());
        assertTrue(seguidores.contains(bruno));
        assertTrue(seguidores.contains(carla));
    }

    @Test
    public void obtenerCantidadSeguidoresTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicio.seguir(bruno, ana);
        servicio.seguir(carla, ana);

        assertEquals(2, servicio.obtenerCantidadSeguidores(ana));
    }

    @Test
    public void obtenerSeguidosTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicio.seguir(ana, bruno);
        servicio.seguir(ana, carla);

        List<Usuario> seguidos = servicio.obtenerSeguidos(ana);

        assertEquals(2, seguidos.size());
        assertTrue(seguidos.contains(bruno));
        assertTrue(seguidos.contains(carla));
    }

    @Test
    public void obtenerCantidadSeguidosTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicio.seguir(ana, bruno);
        servicio.seguir(ana, carla);

        assertEquals(2, servicio.obtenerCantidadSeguidos(ana));
    }


    @Test(expected = IllegalArgumentException.class)
    public void obtenerSeguidoresConUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.obtenerSeguidores(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void obtenerCantidadSeguidoresConUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.obtenerCantidadSeguidores(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void obtenerSeguidosConUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.obtenerSeguidos(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void obtenerCantidadSeguidosConUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.obtenerCantidadSeguidos(null);
    }

    @Test
    public void agregarUsuarioTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");

        servicio.agregarUsuario(ana);

        assertTrue(servicio.getGrafoSeguidores().vertices().contains(ana));
    }

    @Test(expected = IllegalArgumentException.class)
    public void agregarUsuarioNuloTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();

        servicio.agregarUsuario(null);
    }

    @Test
    public void agregarUsuarioExistenteNoLoDuplicaTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");

        servicio.agregarUsuario(ana);
        servicio.agregarUsuario(ana);

        assertEquals(1, servicio.getGrafoSeguidores().vertices().size());
    }

    @Test
    public void gruposDeAmigosMutuosTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicio.seguir(ana, bruno);
        servicio.seguir(bruno, ana);

        servicio.seguir(bruno, carla);
        servicio.seguir(carla, bruno);

        List<Set<Usuario>> grupos = servicio.gruposDeAmigosMutuos();

        assertEquals(1, grupos.size());
        assertEquals(
            new HashSet<>(List.of(ana, bruno, carla)),
            grupos.get(0)
        );
    }

    @Test
    public void gruposDeAmigosMutuosExcluyeUsuariosSinAmistadesMutuasTest() {
        ServicioSeguidores servicio = new ServicioSeguidores();
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicio.agregarUsuario(ana);
        servicio.seguir(bruno, carla);

        assertTrue(servicio.gruposDeAmigosMutuos().isEmpty());
    }
}