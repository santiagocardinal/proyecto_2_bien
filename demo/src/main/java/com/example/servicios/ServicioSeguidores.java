package com.example.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.grafo.DirectedGraph;
import com.example.modelo.Usuario;

//AGREGO YO, LO DISCUTIMOS DESPUES
import com.example.grafo.model.edge.WeightedEdge;

//TENGO QUE AGREGAR PARA LA CONSULTA 3
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

    //AGREGO YO, LO DISCUTIMOS DESPUES
    //===============================================================================================
       
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
    
    // ===================================================================
    // CONSULTA PROPIA 4: Grupos de amigos mutuos
    // ===================================================================

    /**
     * Devuelve los grupos de usuarios unidos por seguimientos mutuos
     * (A sigue a B y B sigue a A). Dos usuarios estan en el mismo grupo
     * si hay una cadena de amistades mutuas entre ellos.
     * Se arma un grafo NO dirigido con una arista por cada amistad mutua
     * (ahi la direccion no importa, porque la relacion es reciproca) y se
     * buscan sus componentes conexas con el recorrido en amplitud (BFS)
     */
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