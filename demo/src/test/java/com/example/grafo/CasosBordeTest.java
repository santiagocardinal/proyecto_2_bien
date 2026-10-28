package com.example.grafo;

import com.example.grafo.model.edge.WeightedEdge;
import com.example.grafo.model.result.IDijkstraResult;
import com.example.grafo.model.result.IFloydWarshallResult;
import com.example.grafo.model.result.Path;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class CasosBordeTest {

    // Grafo no dirigido que tambien es IUndirectedGraph, para usar Prim, Kruskal y BEA
    static class GrafoNoDirigido<V, D> extends Graph<V, D> implements IUndirectedGraph<V, D> {
    }

    private final DirectedGraphAlgorithms algDirigidos = new DirectedGraphAlgorithms();
    private final UndirectedGraphAlgorithmImpl algNoDirigidos = new UndirectedGraphAlgorithmImpl();

    // Crea un grafo dirigido con los vertices indicados
    private DirectedGraph<String, WeightedEdge> dirigido(String... vertices) {
        DirectedGraph<String, WeightedEdge> g = new DirectedGraph<>();
        for (String v : vertices) {
            g.agregarVertice(v);
        }
        return g;
    }

    // Crea un grafo no dirigido con los vertices indicados
    private GrafoNoDirigido<String, WeightedEdge> noDirigido(String... vertices) {
        GrafoNoDirigido<String, WeightedEdge> g = new GrafoNoDirigido<>();
        for (String v : vertices) {
            g.agregarVertice(v);
        }
        return g;
    }

    private WeightedEdge peso(double p) {
        return new WeightedEdge(p);
    }

    
    // GRAFO DIRIGIDO

    @Test
    public void dirigidoVacioTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido();

        assertEquals(0, g.cantidadDeVertices());
        assertEquals(0, g.cantidadDeAristas());
        assertFalse(g.tieneCiclos());
        assertTrue(g.esConexo());
    }

    @Test
    public void dirigidoUnVerticeTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");

        assertTrue(g.esConexo());
        assertFalse(g.tieneCiclos());
        assertTrue(g.successors(g.construirComparable("A")).isEmpty());
        assertTrue(g.predecessors(g.construirComparable("A")).isEmpty());
    }

    @Test
    public void dirigidoConexoYNoConexoTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));
        assertFalse(g.esConexo()); // C queda aislado

        g.agregarArista("C", "B", peso(1));
        assertTrue(g.esConexo()); // ahora todos estan unidos (sin mirar la direccion)
    }

    @Test
    public void dirigidoVerticeInexistenteTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");

        assertNull(g.buscarVertice(g.construirComparable("Z")));
        assertTrue(g.successors(g.construirComparable("Z")).isEmpty());
        assertTrue(g.predecessors(g.construirComparable("Z")).isEmpty());
        assertTrue(g.adyacencias(g.construirComparable("Z")).isEmpty());
        assertFalse(g.agregarArista("A", "Z", peso(1)));
        assertFalse(g.eliminarArista(g.construirComparable("A"), g.construirComparable("Z")));
        assertFalse(g.removerVertice(g.construirComparable("Z")));
    }

    @Test
    public void dirigidoLazoEsCicloTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");
        g.agregarArista("A", "A", peso(1));

        assertTrue(g.tieneCiclos());
    }

    
    // GRAFO NO DIRIGIDO
     

    @Test
    public void noDirigidoVacioTest() {
        Graph<String, WeightedEdge> g = noDirigido();

        assertEquals(0, g.cantidadDeVertices());
        assertFalse(g.tieneCiclos());
        assertTrue(g.esConexo());
    }

    @Test
    public void noDirigidoUnVerticeTest() {
        Graph<String, WeightedEdge> g = noDirigido("A");

        assertTrue(g.esConexo());
        assertFalse(g.tieneCiclos());
        assertTrue(g.adyacencias(g.construirComparable("A")).isEmpty());
    }

    @Test
    public void noDirigidoConexoYNoConexoTest() {
        Graph<String, WeightedEdge> g = noDirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));
        assertFalse(g.esConexo());

        g.agregarArista("B", "C", peso(1));
        assertTrue(g.esConexo());
    }

    @Test
    public void noDirigidoConYSinCiclosTest() {
        Graph<String, WeightedEdge> g = noDirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "C", peso(1));
        assertFalse(g.tieneCiclos()); // una cadena no es un ciclo

        g.agregarArista("C", "A", peso(1));
        assertTrue(g.tieneCiclos());
    }

    
    // DIJKSTRA
    

    @Test
    public void dijkstraUnVerticeTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");
        IDijkstraResult<String> r = algDirigidos.dijkstra(g.construirComparable("A"), g);

        assertEquals(0.0, r.getCost("A"), 0.001);
        assertEquals(List.of("A"), r.getPath("A"));
    }

    @Test
    public void dijkstraOrigenInexistenteTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B");
        g.agregarArista("A", "B", peso(1));

        IDijkstraResult<String> r = algDirigidos.dijkstra(g.construirComparable("Z"), g);

        assertEquals(Double.POSITIVE_INFINITY, r.getCost("B"), 0.001);
        assertTrue(r.getPath("B").isEmpty());
    }

    @Test
    public void dijkstraCaminosDeIgualCostoTest() {
        // A -> B -> D y A -> C -> D cuestan lo mismo (2)
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C", "D");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "D", peso(1));
        g.agregarArista("A", "C", peso(1));
        g.agregarArista("C", "D", peso(1));

        IDijkstraResult<String> r = algDirigidos.dijkstra(g.construirComparable("A"), g);

        assertEquals(2.0, r.getCost("D"), 0.001);
        List<String> camino = r.getPath("D");
        // cualquiera de los dos caminos es correcto
        assertTrue(camino.equals(List.of("A", "B", "D")) || camino.equals(List.of("A", "C", "D")));
    }

    
    // FLOYD Y WARSHALL
    

    @Test
    public void floydNoConectadosTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B");
        IFloydWarshallResult<String> r = algDirigidos.floyd(g);

        assertEquals(Double.POSITIVE_INFINITY, r.getCost("A", "B"), 0.001);
        assertFalse(r.connected("A", "B"));
        assertEquals(0.0, r.getCost("A", "A"), 0.001);
    }

    @Test
    public void floydGrafoVacioTest() {
        IFloydWarshallResult<String> r = algDirigidos.floyd(dirigido());

        assertEquals(Double.POSITIVE_INFINITY, r.getCost("A", "B"), 0.001);
    }

    @Test
    public void warshallAlcanzabilidadTest() {
        // A -> B -> C: A llega a C aunque no haya arista directa
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(5));
        g.agregarArista("B", "C", peso(7));

        IFloydWarshallResult<String> r = algDirigidos.warshall(g);

        assertTrue(r.connected("A", "C"));
        assertFalse(r.connected("C", "A")); // es dirigido
        assertTrue(r.connected("B", "B"));
    }

    @Test
    public void warshallGrafoSinAristasTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B");
        IFloydWarshallResult<String> r = algDirigidos.warshall(g);

        assertFalse(r.connected("A", "B"));
        assertFalse(r.connected("B", "A"));
    }

    
    // CENTRO DEL GRAFO Y EXCENTRICIDAD
    

    @Test
    public void centroDeUnCicloConUnAtajoTest() {
        // B, C y D llegan a A en un paso, asi que A es el centro
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C", "D");
        g.agregarArista("B", "A", peso(1));
        g.agregarArista("C", "A", peso(1));
        g.agregarArista("D", "A", peso(1));
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "C", peso(1));
        g.agregarArista("C", "D", peso(1));

        assertEquals("A", algDirigidos.obtenerCentroGrafo(g));
        assertEquals(1.0, algDirigidos.obtenerExcentricidad(g, g.construirComparable("A")), 0.001);
    }

    @Test
    public void centroDeGrafoVacioTest() {
        assertNull(algDirigidos.obtenerCentroGrafo(dirigido()));
    }

    @Test
    public void centroDeUnVerticeTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");

        assertEquals("A", algDirigidos.obtenerCentroGrafo(g));
        assertEquals(0.0, algDirigidos.obtenerExcentricidad(g, g.construirComparable("A")), 0.001);
    }

    @Test
    public void excentricidadInfinitaSiNoTodosLleganTest() {
        // B no puede llegar a A
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B");
        g.agregarArista("A", "B", peso(1));

        assertEquals(Double.POSITIVE_INFINITY,
                algDirigidos.obtenerExcentricidad(g, g.construirComparable("A")), 0.001);
    }

    @Test
    public void excentricidadDeVerticeInexistenteTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");

        assertEquals(Double.POSITIVE_INFINITY,
                algDirigidos.obtenerExcentricidad(g, g.construirComparable("Z")), 0.001);
    }

    
    // TODOS LOS CAMINOS
    

    @Test
    public void todosLosCaminosVariosCaminosTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C", "D");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "D", peso(1));
        g.agregarArista("A", "C", peso(1));
        g.agregarArista("C", "D", peso(1));
        g.agregarArista("A", "D", peso(5));

        List<Path<String>> caminos = algDirigidos.obtenerTodosLosCaminos(
                g.construirComparable("A"), g.construirComparable("D"), g);

        assertEquals(3, caminos.size());
    }

    @Test
    public void todosLosCaminosSinCaminoTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B");

        assertTrue(algDirigidos.obtenerTodosLosCaminos(
                g.construirComparable("A"), g.construirComparable("B"), g).isEmpty());
    }

    @Test
    public void todosLosCaminosConCicloNoSeRepitenVerticesTest() {
        // el ciclo A -> B -> A no tiene que generar caminos infinitos
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "A", peso(1));
        g.agregarArista("B", "C", peso(1));

        List<Path<String>> caminos = algDirigidos.obtenerTodosLosCaminos(
                g.construirComparable("A"), g.construirComparable("C"), g);

        assertEquals(1, caminos.size());
        assertEquals(List.of("A", "B", "C"), caminos.get(0).getPath());
    }

    @Test
    public void todosLosCaminosVerticeInexistenteTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");

        assertTrue(algDirigidos.obtenerTodosLosCaminos(
                g.construirComparable("A"), g.construirComparable("Z"), g).isEmpty());
    }

    
    // RECORRIDOS (BPF Y BEA)
    

    @Test
    public void bfsVisitaPorNivelesTest() {
        // A -> B, A -> C, B -> D: primero A, despues B y C, por ultimo D
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C", "D");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("A", "C", peso(1));
        g.agregarArista("B", "D", peso(1));

        List<String> orden = new ArrayList<>();
        algDirigidos.recorridoEnAmplitud(g, g.construirComparable("A"), orden::add);

        assertEquals(4, orden.size());
        assertEquals("A", orden.get(0));
        assertEquals("D", orden.get(3));
    }

    @Test
    public void recorridosNoConexoSoloVisitanLoAlcanzableTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));

        List<String> bfs = new ArrayList<>();
        List<String> dfs = new ArrayList<>();
        algDirigidos.recorridoEnAmplitud(g, g.construirComparable("A"), bfs::add);
        algDirigidos.recorridoEnProfundidad(g, g.construirComparable("A"), dfs::add);

        assertEquals(List.of("A", "B"), bfs);
        assertEquals(List.of("A", "B"), dfs);
    }

    @Test
    public void recorridosDesdeVerticeInexistenteTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A");

        List<String> visitados = new ArrayList<>();
        algDirigidos.recorridoEnAmplitud(g, g.construirComparable("Z"), visitados::add);
        algDirigidos.recorridoEnProfundidad(g, g.construirComparable("Z"), visitados::add);

        assertTrue(visitados.isEmpty());
    }

    @Test
    public void recorridoConCicloNoRepiteVerticesTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "C", peso(1));
        g.agregarArista("C", "A", peso(1));

        List<String> visitados = new ArrayList<>();
        algDirigidos.recorridoEnProfundidad(g, g.construirComparable("A"), visitados::add);

        assertEquals(3, visitados.size());
    }

    @Test
    public void beaNoDirigidoRecorreTodasLasComponentesTest() {
        GrafoNoDirigido<String, WeightedEdge> g = noDirigido("A", "B", "C", "D");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("C", "D", peso(1));

        List<String> visitados = new ArrayList<>();
        algNoDirigidos.bea(g, visitados::add);

        // visita a los 4 aunque esten en dos componentes distintas
        assertEquals(Set.of("A", "B", "C", "D"), Set.copyOf(visitados));
        assertEquals(4, visitados.size());
    }

    @Test
    public void beaGrafoVacioTest() {
        List<String> visitados = new ArrayList<>();
        algNoDirigidos.bea(noDirigido(), visitados::add);

        assertTrue(visitados.isEmpty());
    }

    
    // CLASIFICACION TOPOLOGICA
    

    @Test
    public void topologicaConCicloDevuelveVacioTest() {
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "A", peso(1));

        assertTrue(algDirigidos.calcularClasificacionTopologica(g).isEmpty());
    }

    @Test
    public void topologicaGrafoVacioTest() {
        assertTrue(algDirigidos.calcularClasificacionTopologica(dirigido()).isEmpty());
    }

    @Test
    public void topologicaRespetaLasDependenciasTest() {
        // A -> C y B -> C: A y B tienen que ir antes que C
        DirectedGraph<String, WeightedEdge> g = dirigido("A", "B", "C");
        g.agregarArista("A", "C", peso(1));
        g.agregarArista("B", "C", peso(1));

        List<String> orden = algDirigidos.calcularClasificacionTopologica(g);

        assertEquals(3, orden.size());
        assertTrue(orden.indexOf("A") < orden.indexOf("C"));
        assertTrue(orden.indexOf("B") < orden.indexOf("C"));
    }

    
    // PRIM Y KRUSKAL
    

    @Test
    public void kruskalGrafoNoConexoDevuelveBosqueTest() {
        // dos componentes: {A, B} y {C, D}
        GrafoNoDirigido<String, WeightedEdge> g = noDirigido("A", "B", "C", "D");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("C", "D", peso(2));

        IUndirectedGraph<String, WeightedEdge> arbol = algNoDirigidos.kruskal(g);

        assertEquals(4, arbol.cantidadDeVertices());
        assertEquals(2, arbol.cantidadDeAristas()); // una arista por componente
    }

    @Test
    public void primGrafoNoConexoSoloCubreSuComponenteTest() {
        GrafoNoDirigido<String, WeightedEdge> g = noDirigido("A", "B", "C", "D");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("C", "D", peso(2));

        IUndirectedGraph<String, WeightedEdge> arbol = algNoDirigidos.prim(g, g.construirComparable("A"));

        assertEquals(1, arbol.cantidadDeAristas()); // solo la arista A-B
    }

    @Test
    public void kruskalYPrimUnVerticeTest() {
        GrafoNoDirigido<String, WeightedEdge> g = noDirigido("A");

        assertEquals(0, algNoDirigidos.kruskal(g).cantidadDeAristas());
        assertEquals(0, algNoDirigidos.prim(g, g.construirComparable("A")).cantidadDeAristas());
    }

    @Test
    public void kruskalConPesosIgualesTest() {
        // triangulo con todas las aristas de peso 1: el arbol usa 2 de las 3
        GrafoNoDirigido<String, WeightedEdge> g = noDirigido("A", "B", "C");
        g.agregarArista("A", "B", peso(1));
        g.agregarArista("B", "C", peso(1));
        g.agregarArista("C", "A", peso(1));

        IUndirectedGraph<String, WeightedEdge> arbol = algNoDirigidos.kruskal(g);

        assertEquals(2, arbol.cantidadDeAristas());
        assertFalse(arbol.tieneCiclos());
        assertTrue(arbol.esConexo());
    }

    @Test
    public void primOrigenInexistenteTest() {
        GrafoNoDirigido<String, WeightedEdge> g = noDirigido("A", "B");
        g.agregarArista("A", "B", peso(1));

        assertEquals(0, algNoDirigidos.prim(g, g.construirComparable("Z")).cantidadDeAristas());
    }
}