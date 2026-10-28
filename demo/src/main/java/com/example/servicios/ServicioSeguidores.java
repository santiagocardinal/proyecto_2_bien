package com.example.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.grafo.DirectedGraph;
import com.example.modelo.Usuario;

import java.util.HashSet;
import com.example.grafo.DirectedGraphAlgorithms;
import com.example.grafo.Graph;

public class ServicioSeguidores
{
    private final DirectedGraph<Usuario, Object> grafoSeguidores;

    public ServicioSeguidores() 
    {
        this.grafoSeguidores = new DirectedGraph<>();
    }

    public void seguir(Usuario primero, Usuario segundo)
    {
        if (primero == null)
        {
            throw new IllegalArgumentException("El primer usuario no puede ser nulo");
        }

        if (segundo == null)
        {
            throw new IllegalArgumentException("El segundo usuario no puede ser nulo");
        }

        //la idea es que no sean iguales, porque no te vas a seguir a vos mismo 
        if (primero.equals(segundo))
        {
            return;
        }

        //antes se necesita si o si,  agregar los vertices
        grafoSeguidores.agregarVertice(primero);
        grafoSeguidores.agregarVertice(segundo);

        // Si ya lo seguia, no hacemos nada (asi no se cuenta dos veces)
        if (grafoSeguidores.existeArista(primero, segundo))
        {
            return;
        }

        // La arista no tiene dato: el grafo de seguidores no tiene peso
        grafoSeguidores.agregarArista(primero, segundo, null);

        primero.setCantidadSeguidos(primero.getCantidadSeguidos() + 1);
        segundo.setCantidadSeguidores(segundo.getCantidadSeguidores() + 1);
    }
    
    public void dejarDeSeguir(Usuario primero, Usuario segundo)
    {
        if (primero == null)
        {
            throw new IllegalArgumentException("El primer usuario no puede ser nulo");
        }

        if (segundo == null)
        {
            throw new IllegalArgumentException("El segundo usuario no puede ser nulo");
        }
        
        // grafoSeguidores.eliminarArista(primero, segundo);
 
        // Una sola llamada: devuelve true solo si la arista existia y se borro
        boolean desagregado = grafoSeguidores.eliminarArista(primero, segundo);

        if (desagregado)
        {
            primero.setCantidadSeguidos(primero.getCantidadSeguidos() - 1);
            segundo.setCantidadSeguidores(segundo.getCantidadSeguidores() - 1);
        }
    }

    public List<Usuario> obtenerSeguidores(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        Set<Usuario> seguidoresSet = grafoSeguidores.predecessors(usuario);
        
        return new ArrayList<>(seguidoresSet);
    }

    public int obtenerCantidadSeguidores(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        return obtenerSeguidores(usuario).size();
    }

    public List<Usuario> obtenerSeguidos(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        Set<Usuario> seguidosSet = grafoSeguidores.successors(usuario);
        
        return new ArrayList<>(seguidosSet);
    }

    public int obtenerCantidadSeguidos(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        return obtenerSeguidos(usuario).size();

    }
      
    
    public List<Usuario> obtenerSeguidoresMutuos(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        List<Usuario> mutuos = new ArrayList<>();
        List<Usuario> seguidos = obtenerSeguidos(usuario);

        // Un seguidor es mutuo si el usuario tambien lo sigue a el
        for (Usuario seguidor : obtenerSeguidores(usuario))
        {
            if (seguidos.contains(seguidor))
            {
                mutuos.add(seguidor);
            }
        }
        return mutuos;
    }
 
    public void agregarUsuario(Usuario usuario)
    {
        if (usuario == null)
        {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        grafoSeguidores.agregarVertice(usuario);
    }

    public DirectedGraph<Usuario, Object> getGrafoSeguidores()
    {
        return grafoSeguidores;
    }
    
    // CONSULTA  4: Grupos de amigos mutuos
    
    public List<Set<Usuario>> gruposDeAmigosMutuos()
    {
        // Grafo no dirigido de amistades mutuas, con los mismos usuarios
        Graph<Usuario, Object> amistades = new Graph<>();
        for (Usuario usuario : grafoSeguidores.vertices())
        {
            amistades.agregarVertice(usuario);
        }

        // Agregamos una arista solo si el seguimiento es en los dos sentidos
        for (Usuario usuario : grafoSeguidores.vertices())
        {
            for (Usuario seguido : grafoSeguidores.successors(usuario))
            {
                if (grafoSeguidores.existeArista(seguido, usuario))
                {
                    amistades.agregarArista(usuario, seguido, null);
                }
            }
        }

        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();
        Set<Usuario> visitados = new HashSet<>();
        List<Set<Usuario>> grupos = new ArrayList<>();

        // Cada BFS desde un usuario no visitado encuentra un grupo completo
        for (Usuario usuario : amistades.vertices())
        {
            if (!visitados.contains(usuario))
            {
                Set<Usuario> grupo = new HashSet<>();
                algoritmos.recorridoEnAmplitud(amistades, usuario, grupo::add);
                visitados.addAll(grupo);

                // Un usuario solo no es un grupo de amigos
                if (grupo.size() >= 2)
                {
                    grupos.add(grupo);
                }
            }
        }
        return grupos;
    }

}