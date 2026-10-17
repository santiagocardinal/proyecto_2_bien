
package com.example;

import com.example.entorno.Usuario;
import com.example.grafo.DirectedGraph;
import com.example.grafo.IDirectedIGraph;

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

    // -----------------------------------------------------
    // TESTS CALCULAR INFLUENCIA
    // -----------------------------------------------------

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

        Map<Usuario, Integer> resultado =
                servicio.calcularInfluencia();

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

    // -----------------------------------------------------
    // TESTS DETECTAR COMUNIDADES - KOSARAJU
    // -----------------------------------------------------

    @Test
    public void detectarComunidadesGrafoVacio() {

        List<Set<Usuario>> resultado = servicio.detectarComunidades();

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void detectarComunidadesUsuarioAislado() {

        agregarUsuarios(ana);

        List<Set<Usuario>> resultado = servicio.detectarComunidades();

        assertEquals(1, resultado.size());
        assertEquals(Set.of(ana), resultado.get(0));
    }

    @Test
    public void detectarComunidadesTodosAislados() {

        agregarUsuarios(ana, bruno, carlos);

        List<Set<Usuario>> resultado = servicio.detectarComunidades();

        Set<Set<Usuario>> esperado = Set.of(Set.of(ana), Set.of(bruno), Set.of(carlos));

        assertEquals(esperado, new HashSet<>(resultado));
    }

    @Test
    public void detectarComunidadesUnCiclo() {

        agregarUsuarios(ana, bruno, carlos);

        seguir(ana, bruno);
        seguir(bruno, carlos);
        seguir(carlos, ana);

        List<Set<Usuario>> resultado = servicio.detectarComunidades();

        assertEquals(1, resultado.size());

        assertEquals(Set.of(ana, bruno, carlos), resultado.get(0));
    }

    @Test
    public void detectarComunidadesDosComponentesFuertementeConexas() {

        agregarUsuarios(ana, bruno, carlos, diego, elena);

        // Primera comunidad
        seguir(ana, bruno);
        seguir(bruno, carlos);
        seguir(carlos, ana);

        // Segunda comunidad
        seguir(diego, elena);
        seguir(elena, diego);

        // Conexion sin camino de regreso
        seguir(bruno, diego);

        List<Set<Usuario>> resultado = servicio.detectarComunidades();

        Set<Set<Usuario>> esperado = Set.of(Set.of(ana, bruno, carlos), Set.of(diego, elena));

        assertEquals(esperado, new HashSet<>(resultado));
    }

    @Test
    public void detectarComunidadesGrafoSinCiclos() {

        agregarUsuarios(ana, bruno, carlos);

        seguir(ana, bruno);
        seguir(bruno, carlos);

        List<Set<Usuario>> resultado =
                servicio.detectarComunidades();

        Set<Set<Usuario>> esperado = Set.of(Set.of(ana), Set.of(bruno), Set.of(carlos)
        );

        assertEquals(esperado, new HashSet<>(resultado));
    }

    // -----------------------------------------------------
    // TESTS IDENTIFICAR USUARIOS PUENTE
    // -----------------------------------------------------

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
}
