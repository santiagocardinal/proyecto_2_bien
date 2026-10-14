package com.example.grafo;

import com.example.grafo.model.edge.WeightedEdge;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class GraphLoaderTest {

    private File crearArchivo(String contenido) throws IOException {
        File archivo = File.createTempFile("grafo", ".txt");
        archivo.deleteOnExit();
        FileWriter writer = new FileWriter(archivo);
        writer.write(contenido);
        writer.close();
        return archivo;
    }

    @Test
    
    public void CargarGrafotest() throws IOException {
        File aeropuertos = crearArchivo("MVD\nEZE\nGRU\n");
        File conexiones = crearArchivo("# origen,destino,costo\nMVD,EZE,100\nEZE,GRU,250\n");

        DirectedGraph<String, WeightedEdge> grafo = new DirectedGraph<>();
        GraphLoader.cargarGrafoDesdeArchivos(grafo, aeropuertos.getPath(), conexiones.getPath());

        assertEquals(3, grafo.cantidadDeVertices());
        // la linea con # no se tiene que tomar en cuenta
        assertEquals(2, grafo.cantidadDeAristas());
        assertEquals(100.0, grafo.obtenerArista("MVD", "EZE").dato().getWeight(), 0.001);
    }
}
