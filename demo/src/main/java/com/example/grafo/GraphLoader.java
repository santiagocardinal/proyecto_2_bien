package com.example.grafo;

import com.example.grafo.model.edge.WeightedEdge;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphLoader {

    public static void cargarGrafoDesdeArchivos(
            IDirectedIGraph<String, WeightedEdge> grafo, 
            String rutaAeropuertos, 
            String rutaConexiones) {

        // 1. Cargar Vértices desde aeropuertos.txt
        try (BufferedReader br = new BufferedReader(new FileReader(rutaAeropuertos))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    grafo.agregarVertice(linea);
                }
            }
            System.out.println("Aeropuertos cargados con éxito. Total: " + grafo.cantidadDeVertices());
        } catch (IOException e) {
            System.err.println("Error leyendo aeropuertos.txt: " + e.getMessage());
        }

        // 2. Cargar Conexiones desde conexiones.txt
        try (BufferedReader br = new BufferedReader(new FileReader(rutaConexiones))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    // Dividimos por coma según el formato provisto
                    String[] partes = linea.split(",");
                    if (partes.length >= 3) {
                        String origen = partes[0].trim();
                        String destino = partes[1].trim();
                        double costo = Double.parseDouble(partes[2].trim());

                        WeightedEdge datoArista = new WeightedEdge(costo);
                        grafo.agregarArista(origen, destino, datoArista);
                    }
                }
            }
            System.out.println("Conexiones cargadas con éxito. Total aristas: " + grafo.cantidadDeAristas());
        } catch (IOException e) {
            System.err.println("Error leyendo conexiones.txt: " + e.getMessage());
        }
    }
}