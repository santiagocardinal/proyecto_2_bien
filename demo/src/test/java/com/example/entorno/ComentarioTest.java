
package com.example.entorno;

import org.junit.Test;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ComentarioTest {

    @Test
    public void crearComentarioTest() {
        LocalDateTime fecha = LocalDateTime.now();

        Comentario comentario = new Comentario(
            "C1", "Muy buen posteo", fecha
        );

        assertEquals("C1", comentario.getId());
        assertEquals("Muy buen posteo", comentario.getContenido());
        assertEquals(fecha, comentario.getFecha());
    }
}
