
package com.example.entorno;

import java.time.LocalDateTime;

public class Comentario {

    private String id;
    private String contenido;
    private LocalDateTime fecha;

    public Comentario(String id, String contenido, LocalDateTime fecha) {
        this.id = id;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
