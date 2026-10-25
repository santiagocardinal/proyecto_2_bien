package com.example.servicios;

import com.example.modelo.Usuario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ServicioSugerenciasTest {

    @Test
    public void gradosDeSeparacionMismoUsuarioTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");

        assertEquals(0, servicio.gradosDeSeparacion(ana, ana));
    }

    @Test
    public void gradosDeSeparacionUsuariosConectadosDirectamenteTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicioSeguidores.seguir(ana, bruno);

        assertEquals(1, servicio.gradosDeSeparacion(ana, bruno));
    }

    @Test
    public void gradosDeSeparacionUsuariosNoConectadosTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicioSeguidores.agregarUsuario(ana);
        servicioSeguidores.agregarUsuario(bruno);

        assertEquals(-1, servicio.gradosDeSeparacion(ana, bruno));
    }

    @Test(expected = IllegalArgumentException.class)
    public void gradosDeSeparacionConOrigenNuloTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);

        servicio.gradosDeSeparacion(null, new Usuario("bruno"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void gradosDeSeparacionConDestinoNuloTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);

        servicio.gradosDeSeparacion(new Usuario("ana"), null);
    }

    @Test
    public void sugerirUsuariosAmigosDeAmigosTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicioSeguidores.seguir(ana, bruno);
        servicioSeguidores.seguir(bruno, carla);

        List<Usuario> sugerencias = servicio.sugerirUsuarios(ana);

        assertEquals(1, sugerencias.size());
        assertTrue(sugerencias.contains(carla));
    }

    @Test
    public void sugerirUsuariosNoIncluyeUsuariosYaSeguidosTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");

        servicioSeguidores.seguir(ana, bruno);
        servicioSeguidores.seguir(bruno, carla);
        servicioSeguidores.seguir(ana, carla);

        List<Usuario> sugerencias = servicio.sugerirUsuarios(ana);

        assertFalse(sugerencias.contains(carla));
    }

    @Test
    public void sugerirUsuariosNoSeSugiereASiMismoTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicioSeguidores.seguir(ana, bruno);
        servicioSeguidores.seguir(bruno, ana);

        List<Usuario> sugerencias = servicio.sugerirUsuarios(ana);

        assertFalse(sugerencias.contains(ana));
    }

    @Test
    public void sugerirUsuariosNoDuplicaSugerenciasTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");
        Usuario diego = new Usuario("diego");

        servicioSeguidores.seguir(ana, bruno);
        servicioSeguidores.seguir(ana, carla);
        servicioSeguidores.seguir(bruno, diego);
        servicioSeguidores.seguir(carla, diego);

        List<Usuario> sugerencias = servicio.sugerirUsuarios(ana);

        assertEquals(1, sugerencias.size());
        assertTrue(sugerencias.contains(diego));
    }

    @Test
    public void sugerirUsuariosSinAmigosDeAmigosDevuelveListaVaciaTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");

        servicioSeguidores.seguir(ana, bruno);

        List<Usuario> sugerencias = servicio.sugerirUsuarios(ana);

        assertTrue(sugerencias.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sugerirUsuariosConUsuarioNuloTest() {
        ServicioSeguidores servicioSeguidores = new ServicioSeguidores();
        ServicioSugerencias servicio = new ServicioSugerencias(servicioSeguidores);

        servicio.sugerirUsuarios(null);
    }
}