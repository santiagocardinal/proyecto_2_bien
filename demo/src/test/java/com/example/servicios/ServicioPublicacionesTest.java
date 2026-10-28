package com.example.servicios;

import com.example.modelo.Comentario;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ServicioPublicacionesTest {

    private ServicioPublicaciones servicio;
    private Usuario juan;
    private Usuario maria;
    private Usuario pedro;
    private Usuario carlos;
    private List<Usuario> todosLosUsuarios;

    @Before
    public void setUp() {
        servicio = new ServicioPublicaciones();
        juan = new Usuario("juan");
        maria = new Usuario("maria");
        pedro = new Usuario("pedro");
        carlos = new Usuario("carlos");
        todosLosUsuarios = Arrays.asList(juan, maria, pedro, carlos);
    }

    @Test
    public void testPublicarGeneraIdYRegistraMencion() {
        Posteo posteo = servicio.publicar(juan, "Hola a todos, en especial a @maria", todosLosUsuarios);

        assertNotNull(posteo);
        assertEquals("P-1", posteo.getId());
        assertEquals(1, servicio.getPublicaciones().size());
        assertEquals(1, servicio.getMenciones().size());
        assertEquals(maria, servicio.getMenciones().get(0).getUsuarioMencionado());
    }

    @Test
    public void testComentarIncrementaPesoDeInteraccion() {
        Posteo posteo = servicio.publicar(juan, "Mi primer posteo", todosLosUsuarios);
        Comentario comentario = servicio.comentar(maria, posteo, "Excelente posteo @juan", todosLosUsuarios);

        assertNotNull(comentario);
        assertEquals("C-1", comentario.getId());
        assertEquals(1, servicio.getComentarios().size());

        var arista = servicio.getGrafoInteracciones().obtenerArista(v -> v.equals(maria) ? 0 : -1,
                v -> v.equals(juan) ? 0 : -1);
        assertNotNull(arista);
        assertTrue(arista.dato() >= 1);
    }

    @Test
    public void testCompartirSumaInteraccionEnElGrafo() {
        Posteo posteo = servicio.publicar(juan, "Noticia importante", todosLosUsuarios);
        servicio.compartir(pedro, posteo);

        var arista = servicio.getGrafoInteracciones().obtenerArista(v -> v.equals(pedro) ? 0 : -1,
                v -> v.equals(juan) ? 0 : -1);
        assertNotNull(arista);
        assertEquals(Integer.valueOf(1), arista.dato());
    }

    @Test
    public void testConstruirFeedIncluyePropiosSeguidosYMenciones() {
        Posteo p1 = servicio.publicar(juan, "Post de Juan", todosLosUsuarios);
        Posteo p2 = servicio.publicar(maria, "Post de Maria con @pedro", todosLosUsuarios);
        servicio.publicar(carlos, "Post de Carlos no seguido", todosLosUsuarios);

        Set<Usuario> seguidosDePedro = new HashSet<>();
        seguidosDePedro.add(juan);

        List<Posteo> feedPedro = servicio.construirFeed(pedro, seguidosDePedro);

        assertTrue(feedPedro.contains(p1));
        assertTrue(feedPedro.contains(p2));
        assertEquals(2, feedPedro.size());
    }

    @Test
    public void testCalcularAlcanceBFS() {
        Posteo posteo = servicio.publicar(juan, "Lanzamiento oficial @maria", todosLosUsuarios);
        servicio.comentar(pedro, posteo, "Apoyo la causa", todosLosUsuarios);

        int alcance = servicio.calcularAlcance(posteo);

        assertTrue(alcance >= 2);
    }
}
