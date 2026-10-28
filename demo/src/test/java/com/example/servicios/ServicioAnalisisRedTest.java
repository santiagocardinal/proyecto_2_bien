
package com.example.servicios;

import com.example.grafo.DirectedGraph;
import com.example.grafo.IDirectedIGraph;
import com.example.modelo.Usuario;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ServicioAnalisisRedTest {

    private IDirectedIGraph<Usuario, String> grafo;
    private ServicioAnalisisRed<String> servicio;

    private Usuario ana;
    private Usuario bruno;
    private Usuario carlos;
    private Usuario diego;
    private Usuario elena;
    private Usuario federico;

    @Before
    public void setUp() {

        grafo = new DirectedGraph<>();
        servicio = new ServicioAnalisisRed<>(grafo);

        ana = crearUsuario("Ana");
        bruno = crearUsuario("Bruno");
        carlos = crearUsuario("Carlos");
        diego = crearUsuario("Diego");
        elena = crearUsuario("Elena");
        federico = crearUsuario("Federico");
    }

    private Usuario crearUsuario(String nombre) {
        return new Usuario(nombre);
    }

    private void agregarUsuarios(Usuario... usuarios) {
        for (Usuario usuario : usuarios) {
            assertTrue(grafo.agregarVertice(usuario));
        }
    }

    // A sigue a B: A -> B
    private void seguir(Usuario origen, Usuario destino) {
        assertTrue(grafo.agregarArista(origen, destino, "seguimiento"));
    }

    
    @Test
    public void calcularInfluenciaGrafoVacio() {

        Map<Usuario, Integer> resultado = servicio.calcularInfluencia();

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void calcularInfluenciaUsuarioSinSeguidores() {

        agregarUsuarios(ana);

        Map<Usuario, Integer> resultado = servicio.calcularInfluencia();

        assertEquals(1, resultado.size());
        assertEquals(Integer.valueOf(0), resultado.get(ana));
    }

    @Test
    public void calcularInfluenciaVariosUsuarios() {

        agregarUsuarios(ana, bruno, carlos, diego);

        seguir(ana, bruno);
        seguir(carlos, bruno);
        seguir(diego, bruno);
        seguir(ana, carlos);

        Map<Usuario, Integer> resultado = servicio.calcularInfluencia();

        assertEquals(4, resultado.size());

        assertEquals(Integer.valueOf(0), resultado.get(ana));
        assertEquals(Integer.valueOf(3), resultado.get(bruno));
        assertEquals(Integer.valueOf(1), resultado.get(carlos));
        assertEquals(Integer.valueOf(0), resultado.get(diego));
    }

    @Test
    public void calcularInfluenciaSeguimientoMutuo() {

        agregarUsuarios(ana, bruno);

        seguir(ana, bruno);
        seguir(bruno, ana);

        Map<Usuario, Integer> resultado = servicio.calcularInfluencia();

        assertEquals(Integer.valueOf(1), resultado.get(ana));
        assertEquals(Integer.valueOf(1), resultado.get(bruno));
    }

    

    @Test
    public void identificarUsuariosPuenteGrafoVacio() {

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void identificarUsuariosPuenteUsuarioAislado() {

        agregarUsuarios(ana);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void identificarUsuariosPuenteCadena() {

        agregarUsuarios(ana, bruno, carlos, diego);

        seguir(ana, bruno);
        seguir(bruno, carlos);
        seguir(carlos, diego);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertEquals(Set.of(bruno, carlos), resultado);
    }

    @Test
    public void identificarUsuariosPuenteCiclo() {

        agregarUsuarios(ana, bruno, carlos);

        seguir(ana, bruno);
        seguir(bruno, carlos);
        seguir(carlos, ana);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void identificarUsuariosPuenteDosGruposConectados() {

        agregarUsuarios(ana, bruno, carlos, diego, elena, federico);

        // Primer grupo
        seguir(ana, bruno);
        seguir(bruno, carlos);
        seguir(carlos, ana);

        // Segundo grupo
        seguir(diego, elena);
        seguir(elena, federico);
        seguir(federico, diego);

        // Unica conexion entre grupos
        seguir(bruno, diego);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertEquals(Set.of(bruno, diego), resultado);
    }

    @Test
    public void identificarUsuariosPuenteRaizConDosHijos() {

        agregarUsuarios(ana, bruno, carlos);

        seguir(ana, bruno);
        seguir(ana, carlos);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertEquals(Set.of(ana), resultado);
    }

    @Test
    public void identificarUsuariosPuenteComponentesDesconectadas() {

        agregarUsuarios(ana, bruno, carlos, diego, elena, federico);

        // Primera componente: cadena
        seguir(ana, bruno);
        seguir(bruno, carlos);

        // Segunda componente: ciclo
        seguir(diego, elena);
        seguir(elena, federico);
        seguir(federico, diego);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();

        assertEquals(Set.of(bruno), resultado);
    }

    @Test
    public void identificarUsuariosPuenteSeguimientosMutuos() {

        agregarUsuarios(ana, bruno, carlos);

        seguir(ana, bruno);
        seguir(bruno, ana);

        seguir(bruno, carlos);
        seguir(carlos, bruno);

        Set<Usuario> resultado = servicio.identificarUsuariosPuente();
        assertEquals(Set.of(bruno), resultado);
    }
     
    @Test
    public void detectarComunidadesGrafoVacio() {
        assertTrue(servicio.detectarComunidades().isEmpty());
    }

    @Test
    public void detectarComunidadesUsuarioAislado() {
        agregarUsuarios(ana);

        List<Set<Usuario>> comunidades = servicio.detectarComunidades();

        assertEquals(1, comunidades.size());
        assertEquals(Set.of(ana), comunidades.get(0));
    }

    @Test
    public void detectarComunidadesUnCiclo() {
        // ana -> bruno -> carlos -> ana: los tres se alcanzan entre si
        agregarUsuarios(ana, bruno, carlos);
        seguir(ana, bruno);
        seguir(bruno, carlos);
        seguir(carlos, ana);

        List<Set<Usuario>> comunidades = servicio.detectarComunidades();

        assertEquals(1, comunidades.size());
        assertEquals(Set.of(ana, bruno, carlos), comunidades.get(0));
    }

    @Test
    public void detectarComunidadesGrafoSinCiclos() {
        // ana -> bruno -> carlos: nadie vuelve, cada uno es su propia comunidad
        agregarUsuarios(ana, bruno, carlos);
        seguir(ana, bruno);
        seguir(bruno, carlos);

        assertEquals(3, servicio.detectarComunidades().size());
    }

    @Test
    public void detectarComunidadesDosGruposUnidosEnUnSentido() {
        // grupo 1: ana <-> bruno, grupo 2: diego <-> elena, y bruno -> diego
        agregarUsuarios(ana, bruno, diego, elena);
        seguir(ana, bruno);
        seguir(bruno, ana);
        seguir(diego, elena);
        seguir(elena, diego);
        seguir(bruno, diego);

        Set<Set<Usuario>> comunidades = new HashSet<>(servicio.detectarComunidades());

        assertEquals(2, comunidades.size());
        assertTrue(comunidades.contains(Set.of(ana, bruno)));
        assertTrue(comunidades.contains(Set.of(diego, elena)));
    }

    
    // CONSULTA 2: CENTRO DE CADA COMUNIDAD

    @Test
    public void centrosDeComunidadesGrafoVacio() {
        assertTrue(servicio.centrosDeComunidades().isEmpty());
    }

    @Test
    public void centroDeComunidadDeUnSoloUsuario() {
        agregarUsuarios(ana);

        Map<Set<Usuario>, Usuario> centros = servicio.centrosDeComunidades();

        assertEquals(ana, centros.get(Set.of(ana)));
    }

    @Test
    public void centroDeComunidadEsElMasCercanoATodos() {
        // bruno, carlos y diego siguen a ana, y ana sigue a todos: ana es el centro
        agregarUsuarios(ana, bruno, carlos, diego);
        seguir(bruno, ana);
        seguir(carlos, ana);
        seguir(diego, ana);
        seguir(ana, bruno);
        seguir(ana, carlos);
        seguir(ana, diego);

        Map<Set<Usuario>, Usuario> centros = servicio.centrosDeComunidades();

        assertEquals(ana, centros.get(Set.of(ana, bruno, carlos, diego)));
    }
}
