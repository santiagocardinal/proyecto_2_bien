package com.example.modelo;

import java.util.Objects;

public class Usuario implements Comparable<Usuario>
{
    private String nombreUsuario;
    private int cantidadPost;
    private int cantidadSeguidores;
    private int cantidadSeguidos;


    public Usuario(String nombreUsuario)
    {
        this.nombreUsuario = nombreUsuario;
        this.cantidadPost = 0;
        this.cantidadSeguidores = 0;
        this.cantidadSeguidos = 0;
    }

    public String getNombreUsuario()
    {
        return nombreUsuario;
    }

    public int getCantidadPost()
    {
        return cantidadPost;
    }

    public int getCantidadSeguidores()
    {
        return cantidadSeguidores;
    }

    public int getCantidadSeguidos()
    {
        return cantidadSeguidos;
    }

    public void setNombreUsuario(String nombreUsuario)
    {
        this.nombreUsuario = nombreUsuario;
    }

    public void setCantidadPost(int cantidadPost)
    {
        this.cantidadPost = cantidadPost;
    }

    public void setCantidadSeguidores(int cantidadSeguidores)
    {
        this.cantidadSeguidores = cantidadSeguidores;
    }

    public void setCantidadSeguidos(int cantidadSeguidos)
    {
        this.cantidadSeguidos = cantidadSeguidos;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Usuario)) 
        {
            return false;
        }
        Usuario otroUsuario = (Usuario) obj;
        return Objects.equals(this.nombreUsuario, otroUsuario.nombreUsuario);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(nombreUsuario);
    }

    @Override
    public int compareTo(Usuario otro) {
        if (otro == null) {
            return 1;
        }
        return this.nombreUsuario.compareTo(otro.nombreUsuario);
    }
}