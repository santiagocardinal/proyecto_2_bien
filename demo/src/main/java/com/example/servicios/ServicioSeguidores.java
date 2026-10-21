package com.example.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.grafo.DirectedGraph;
import com.example.modelo.Usuario;

//AGREGO YO, LO DISCUTIMOS DESPUES
import com.example.grafo.model.edge.WeightedEdge;

public class ServicioSeguidores
{
    //CAMBIO:     private final DirectedGraph<Usuario, Object> grafoSeguidores;
    //POR ESTO:
    private final DirectedGraph<Usuario, WeightedEdge> grafoSeguidores;

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

        //antes se necesita si o si, obviamente, agregar los vertices
        grafoSeguidores.agregarVertice(primero);
        grafoSeguidores.agregarVertice(segundo);
        
        //AGREGO LO DE ABAJO
        grafoSeguidores.agregarArista(primero, segundo, new WeightedEdge(1.0));
        //Tengo que sacar esto
        /*boolean agregado = grafoSeguidores.agregarArista(primero, segundo, null);

        if (agregado)
        {
            primero.setCantidadSeguidos(primero.getCantidadSeguidos() + 1);
            segundo.setCantidadSeguidores(segundo.getCantidadSeguidores() + 1);
        }*/
       
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
        
        //AGREGO LO DE ABAJO
        grafoSeguidores.eliminarArista(primero, segundo);

        //SACO
        /* 
        boolean desagregado = grafoSeguidores.eliminarArista(primero, segundo);

        if (desagregado)
        {
            primero.setCantidadSeguidos(primero.getCantidadSeguidos() - 1);
            segundo.setCantidadSeguidores(segundo.getCantidadSeguidores() - 1);

        }*/
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

    public DirectedGraph<Usuario, WeightedEdge> getGrafoSeguidores()
    {
        return grafoSeguidores;
    }
}