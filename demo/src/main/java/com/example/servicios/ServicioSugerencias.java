package com.example.servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.example.modelo.Usuario;
//IMPORTS QUE NECESITO PARA LA CONSULTA 5
import com.example.grafo.DirectedGraphAlgorithms;
import com.example.grafo.model.result.Path;


public class ServicioSugerencias 
{
    private final ServicioSeguidores servicioSeguidores;

    public ServicioSugerencias(ServicioSeguidores servicioSeguidores) {
        this.servicioSeguidores = servicioSeguidores;
    }

    public int gradosDeSeparacion(Usuario origen, Usuario destino)
    {
        if (origen == null)
        {
            throw new IllegalArgumentException("El usuario origen no puede ser nulo");
        }

        if (destino == null)
        {
            throw new IllegalArgumentException("El usuario destino no puede ser nulo");
        }

        if (origen.equals(destino))
        {
            return 0;
        }

        Queue<Usuario> cola = new LinkedList<>();
        Set<Usuario> visitados = new HashSet<>();
        Map<Usuario, Integer> distancias = new HashMap<>();

        cola.add(origen);
        visitados.add(origen);
        distancias.put(origen, 0);

        while (!cola.isEmpty())
        {
            Usuario actual = cola.poll();

            // Recorremos los usuarios a los que sigue el usuario actual
            for (Usuario sucesor : servicioSeguidores.obtenerSeguidos(actual))
            {
                if (!visitados.contains(sucesor))
                {
                    visitados.add(sucesor);
                    
                    int nuevaDistancia = distancias.get(actual) + 1;
                    distancias.put(sucesor, nuevaDistancia);
                    
                    cola.add(sucesor);

                    // Si el sucesor que acabamos de descubrir es nuestro destino, 
                    // devolvemos la distancia mínima de inmediato
                    if (sucesor.equals(destino))
                    {
                        return nuevaDistancia;
                    }
                }
            }
        }
        // Si la cola se vacía y nunca encontramos al destino, no están conectados
        return -1;
    } 
    
    public List<Usuario> sugerirUsuarios(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        Set<Usuario> sugerenciasSet = new HashSet<>();
        List<Usuario> seguidosDirectos = servicioSeguidores.obtenerSeguidos(usuario);

        for (Usuario amigo : seguidosDirectos)
        {
            List<Usuario> amigosDeAmigos = servicioSeguidores.obtenerSeguidos(amigo);

            for (Usuario candidato : amigosDeAmigos)
            {
                if (!candidato.equals(usuario) && !seguidosDirectos.contains(candidato))
                {
                    sugerenciasSet.add(candidato);
                }
            }
        }
        return new ArrayList<>(sugerenciasSet);
    }
        // =====================================================================
    // CONSULTA PROPIA 5: Cadenas de contacto
    // =====================================================================

    /**
     * Devuelve todas las formas distintas de llegar de un usuario a otro
     * siguiendo la relacion "sigue a", sin repetir usuarios.
     * Complementa a gradosDeSeparacion: aquel da el camino mas corto,
     * esta consulta da todos.
     * Usa "todos los caminos" de la biblioteca (DFS con backtracking).
     * Complejidad: exponencial en el peor caso, solo apta para redes chicas.
     */
    public List<List<Usuario>> cadenasDeContacto(Usuario origen, Usuario destino) {
        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();

        // La biblioteca devuelve cada camino con su costo
        List<Path<Usuario>> caminos = algoritmos.obtenerTodosLosCaminos(origen, destino, servicioSeguidores.getGrafoSeguidores());

        // Nos quedamos solo con la lista de usuarios de cada camino
        List<List<Usuario>> cadenas = new ArrayList<>();
        for (Path<Usuario> camino : caminos) {
            cadenas.add(camino.getPath());
        }
        return cadenas;
    }
}
