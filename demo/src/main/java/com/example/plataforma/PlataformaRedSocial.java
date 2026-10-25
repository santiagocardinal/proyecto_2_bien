package com.example.plataforma;

import com.example.modelo.Comentario;
import com.example.modelo.Posteo;
import com.example.modelo.Usuario;
import com.example.servicios.ServicioAnalisisRed;
import com.example.servicios.ServicioDifusion;
import com.example.servicios.ServicioPublicaciones;
import com.example.servicios.ServicioSeguidores;
import com.example.servicios.ServicioSugerenciasTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlataformaRedSocial {

    private final Map<String, Usuario> usuarios;
    private final Map<String, Posteo> posteos;

    private final ServicioSeguidores servicioSeguidores;       // grafoSeguidores
    private final ServicioPublicaciones servicioPublicaciones; // grafoInteracciones
    private final ServicioDifusion servicioDifusion;           // grafoDifusion
    private final ServicioSugerenciasTest servicioSugerencias;
    private final ServicioAnalisisRed<Object> servicioAnalisisRed;

    public PlataformaRedSocial() {
        this.usuarios = new HashMap<>();
        this.posteos = new HashMap<>();

        this.servicioSeguidores = new ServicioSeguidores();
        this.servicioPublicaciones = new ServicioPublicaciones();

        // Todos trabajan sobre el MISMO grafo de seguidores
        this.servicioDifusion = new ServicioDifusion(servicioSeguidores);
        this.servicioSugerencias = new ServicioSugerenciasTest(servicioSeguidores);
        this.servicioAnalisisRed = new ServicioAnalisisRed<>(servicioSeguidores.getGrafoSeguidores());
    }

    // Usuarios - CLASES DE NICO :(

    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null || usuarios.containsKey(usuario.getNombreUsuario())) {
            return false;
        }
        usuarios.put(usuario.getNombreUsuario(), usuario);
        servicioSeguidores.agregarUsuario(usuario);
        return true;
    }

    public Usuario obtenerUsuario(String nombreUsuario) {
        return usuarios.get(nombreUsuario);
    }

    public List<Usuario> obtenerUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public void seguirUsuario(Usuario seguidor, Usuario seguido) {
        validarUsuario(seguidor);
        validarUsuario(seguido);
        servicioSeguidores.seguir(seguidor, seguido);
    }

    public void dejarDeSeguirUsuario(Usuario seguidor, Usuario seguido) {
        validarUsuario(seguidor);
        validarUsuario(seguido);
        servicioSeguidores.dejarDeSeguir(seguidor, seguido);
    }

    public List<Usuario> obtenerSeguidores(Usuario usuario) {
        validarUsuario(usuario);
        return servicioSeguidores.obtenerSeguidores(usuario);
    }

    public List<Usuario> obtenerSeguidos(Usuario usuario) {
        validarUsuario(usuario);
        return servicioSeguidores.obtenerSeguidos(usuario);
    }

    //Posteos  - CLASES DE RODRIGO

    public boolean registrarPosteo(Posteo posteo) {
        if (posteo == null || posteo.getId() == null) {
            return false;
        }
        if (posteos.containsKey(posteo.getId())) {
            return false;
        }
        posteos.put(posteo.getId(), posteo);
        return true;
    }

    public Posteo obtenerPosteo(String id) {
        return posteos.get(id);
    }

    // Publicaciones - CLASES DE SEBA

    public Posteo publicar(Usuario autor, String contenido) {
        validarUsuario(autor);
        Posteo posteo = servicioPublicaciones.publicar(autor, contenido, obtenerUsuarios());
        registrarPosteo(posteo);
        return posteo;
    }

    public Comentario comentar(Usuario autor, Posteo posteo, String contenido) {
        validarUsuario(autor);
        validarPosteo(posteo);
        return servicioPublicaciones.comentar(autor, posteo, contenido, obtenerUsuarios());
    }

    /**
     * Solo se puede compartir un posteo que le llegó al usuario.
     * Retorna false si no le llegó.
     */
    public boolean compartir(Usuario usuario, Posteo posteo) {
        validarUsuario(usuario);
        validarPosteo(posteo);

        if (!servicioDifusion.leLlega(usuario, posteo)) {
            return false;
        }
        servicioPublicaciones.compartir(usuario, posteo);       // suma interacción
        servicioDifusion.registrarCompartido(usuario, posteo);  // registra para la difusión
        return true;
    }

    /**
     * Feed: posteos propios, de seguidos, donde me mencionaron
     * y los que compartió alguien que sigo.
     */
    public List<Posteo> obtenerFeed(Usuario usuario) {
        validarUsuario(usuario);
        List<Usuario> seguidos = servicioSeguidores.obtenerSeguidos(usuario);

        List<Posteo> feed = servicioPublicaciones.construirFeed(usuario, new HashSet<>(seguidos));

        for (Posteo posteo : servicioPublicaciones.getPublicaciones()) {
            if (!feed.contains(posteo) && servicioDifusion.fueCompartidoPorAlguno(posteo, seguidos)) {
                feed.add(posteo);
            }
        }
        return feed;
    }

    // Difusión - YO

    public Set<Usuario> calcularAlcance(Posteo posteo) {
        validarPosteo(posteo);
        return servicioDifusion.calcularAlcance(posteo);
    }

    //Sugerencias - CLASE LUJI

    public int gradosDeSeparacion(Usuario origen, Usuario destino) {
        validarUsuario(origen);
        validarUsuario(destino);
        return servicioSugerencias.gradosDeSeparacion(origen, destino);
    }

    public List<Usuario> sugerirUsuarios(Usuario usuario) {
        validarUsuario(usuario);
        return servicioSugerencias.sugerirUsuarios(usuario);
    }

    //Análisis de red - CLASES HERNAN

    public Map<Usuario, Integer> calcularInfluencia() {
        return servicioAnalisisRed.calcularInfluencia();
    }

    public List<Set<Usuario>> detectarComunidades() {
        return servicioAnalisisRed.detectarComunidades();
    }

    public Set<Usuario> identificarUsuariosPuente() {
        return servicioAnalisisRed.identificarUsuariosPuente();
    }

    // =====================================================================
    // CONSULTAS PROPIAS DEL GRUPO
    // Cada una responde una pregunta del dominio usando la biblioteca de grafos.
    // =====================================================================

    /**
     * Consulta 1 - Camino de difusion.
     * ¿Por donde le llego este posteo a este usuario?
     * Algoritmo: Dijkstra. Implementada en ServicioDifusion.
     */
    public List<Usuario> caminoDeDifusion(Posteo posteo, Usuario destino) {
        validarPosteo(posteo);
        validarUsuario(destino);
        return servicioDifusion.caminoDeDifusion(posteo, destino);
    }

    /**
     * Consulta 2 - Centro de cada comunidad.
     * ¿Quien es el usuario mas cercano a todos dentro de su grupo?
     * Algoritmo: Kosaraju + centro del grafo (Floyd). Implementada en ServicioAnalisisRed.
     */
    public Map<Set<Usuario>, Usuario> centrosDeComunidades() {
        return servicioAnalisisRed.centrosDeComunidades();
    }

    /**
     * Consulta 3 - Circulo de interaccion.
     * ¿Con quienes esta conectado un usuario por interacciones, directas o indirectas?
     * Algoritmo: recorrido en profundidad (DFS). Implementada en ServicioPublicaciones.
     */
    public Set<Usuario> circuloDeInteraccion(Usuario usuario) {
        validarUsuario(usuario);
        return servicioPublicaciones.circuloDeInteraccion(usuario);
    }

    /**
     * Consulta 4 - Grupos de amigos mutuos.
     * ¿Que grupos de usuarios estan unidos por seguimientos mutuos?
     * Algoritmo: recorrido en amplitud (BFS) sobre un grafo no dirigido. Implementada en ServicioSeguidores.
     */
    public List<Set<Usuario>> gruposDeAmigosMutuos() {
        return servicioSeguidores.gruposDeAmigosMutuos();
    }

    /**
     * Consulta 5 - Orden de llegada.
     * ¿En que orden le fue llegando un posteo a los usuarios?
     * Algoritmo: recorrido en amplitud (BFS). Implementada en ServicioDifusion.
     */
    public List<Usuario> ordenDeLlegada(Posteo posteo) {
        validarPosteo(posteo);
        return servicioDifusion.ordenDeLlegada(posteo);
    }

    // Validaciones

    private void validarUsuario(Usuario usuario) {
        if (usuario == null || !usuarios.containsKey(usuario.getNombreUsuario())) {
            throw new IllegalArgumentException("El usuario " + usuario + " no está registrado");
        }
    }

    private void validarPosteo(Posteo posteo) {
        if (posteo == null || !posteos.containsKey(posteo.getId())) {
            throw new IllegalArgumentException("El posteo no está registrado");
        }
    }
}