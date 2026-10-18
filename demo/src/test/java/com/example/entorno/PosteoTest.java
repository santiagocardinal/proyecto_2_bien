
package com.example.entorno;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class PosteoTest {

    private Posteo posteo;

    @Before
    public void setUp() {
        posteo = new Posteo("P1", "Mi primer posteo");
    }

    @Test
    public void crearPosteoTest() {
        assertEquals("P1", posteo.getId());
        assertEquals("Mi primer posteo", posteo.getContenido());
        assertTrue(posteo.getComentarios().isEmpty());
        assertTrue(posteo.getMenciones().isEmpty());
    }

    @Test
    public void agregarComentarioTest() {
        Comentario comentario = new Comentario(
            "C1", "Buen posteo", LocalDateTime.now()
        );

        posteo.agregarComentario(comentario);

        assertEquals(1, posteo.getComentarios().size());
        assertSame(comentario, posteo.getComentarios().get(0));
    }

    @Test
    public void agregarVariosComentariosTest() {
        Comentario c1 = new Comentario(
            "C1", "Hola", LocalDateTime.now()
        );

        Comentario c2 = new Comentario(
            "C2", "Muy bueno", LocalDateTime.now()
        );

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
        Usuario usuario = new Usuario("Juan");

        posteo.agregarMencion(usuario);

        assertEquals(1, posteo.getMenciones().size());

        Menciones mencion = posteo.getMenciones().get(0);

        assertSame(posteo, mencion.getPosteo());
        assertSame(usuario, mencion.getUsuarioMencionado());
    }

    @Test
    public void agregarVariasMencionesTest() {
        Usuario juan = new Usuario("Juan");
        Usuario pedro = new Usuario("Pedro");

        posteo.agregarMencion(juan);
        posteo.agregarMencion(pedro);

        assertEquals(2, posteo.getMenciones().size());
    }

    @Test
    public void agregarMencionNullTest() {
        posteo.agregarMencion(null);

        assertTrue(posteo.getMenciones().isEmpty());
    }

    @Test
    public void noModificarListaOriginalTest() {
        Comentario comentario = new Comentario(
            "C1", "Hola", LocalDateTime.now()
        );
        Usuario usuario = new Usuario("Juan");

        posteo.agregarComentario(comentario);
        posteo.agregarMencion(usuario);

        posteo.getComentarios().clear();
        posteo.getMenciones().clear();

        assertEquals(1, posteo.getComentarios().size());
        assertEquals(1, posteo.getMenciones().size());
    }
}
