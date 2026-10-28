package com.example.grafo;
import com.example.grafo.model.result.Path;
import com.example.grafo.model.edge.WeightedEdge;
import com.example.grafo.model.result.IDijkstraResult;
import com.example.grafo.model.result.IFloydWarshallResult;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DirectedGraphAlgorithmsTest {

    private DirectedGraph<String, WeightedEdge> grafo;
    private DirectedGraphAlgorithms algoritmos;

    @Before
    public void setUp() {
        grafo = new DirectedGraph<>();
        algoritmos = new DirectedGraphAlgorithms();

        grafo.agregarVertice("A");
        grafo.agregarVertice("B");
        grafo.agregarVertice("C");
        grafo.agregarVertice("D");

        grafo.agregarArista("A", "B", new WeightedEdge(1));
        grafo.agregarArista("B", "C", new WeightedEdge(2));
        grafo.agregarArista("A", "C", new WeightedEdge(5));
        grafo.agregarArista("C", "D", new WeightedEdge(1));
    }

    @Test 
    
    public void Dijkstratest() {
        IDijkstraResult<String> res = algoritmos.dijkstra(grafo.construirComparable("A"), grafo);

        // A-B-C-D sale 4, mas barato que ir directo A-C
        assertEquals(4.0, res.getCost("D"), 0.001);
        assertEquals(List.of("A", "B", "C", "D"), res.getPath("D"));
    }

    @Test

    public void DijkstraNoAlcanzabletest() {
        IDijkstraResult<String> res = algoritmos.dijkstra(grafo.construirComparable("D"), grafo);
        // desde D no se llega a ningun lado
        assertEquals(Double.POSITIVE_INFINITY, res.getCost("A"), 0.001);
    }

    @Test
    public void Floydtest() {
        IFloydWarshallResult<String> res = algoritmos.floyd(grafo);

        assertEquals(3.0, res.getCost("A", "C"), 0.001);
        assertEquals(List.of("A", "B", "C"), res.getPath("A", "C"));
        assertEquals(0.0, res.getCost("B", "B"), 0.001);
    }

    @Test
    public void RecorridoEnProfundidadtest() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnProfundidad(grafo, grafo.construirComparable("A"), visitados::add);

        assertEquals(4, visitados.size());
        assertEquals("A", visitados.get(0));
    }

    @Test
    public void ClasificacionTopologicatest() {
        List<String> orden = algoritmos.calcularClasificacionTopologica(grafo);
        assertEquals(List.of("A", "B", "C", "D"), orden);
    }
 
    // Crea un grafo dirigido nuevo con los vertices indicados
    private DirectedGraph<String, WeightedEdge> nuevoGrafo(String... vertices) {
        DirectedGraph<String, WeightedEdge> g = new DirectedGraph<>();
        for (String v : vertices) {
            g.agregarVertice(v);
        }
        return g;
    }

    //  Dijkstra

    @Test
    public void dijkstraUnVerticeTest() {
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A");
        IDijkstraResult<String> res = algoritmos.dijkstra(g.construirComparable("A"), g);

        assertEquals(0.0, res.getCost("A"), 0.001);
        assertEquals(List.of("A"), res.getPath("A"));
    }

    @Test
    public void dijkstraOrigenInexistenteTest() {
        IDijkstraResult<String> res = algoritmos.dijkstra(grafo.construirComparable("Z"), grafo);

        assertEquals(Double.POSITIVE_INFINITY, res.getCost("B"), 0.001);
        assertTrue(res.getPath("B").isEmpty());
    }

    @Test
    public void dijkstraCaminosDeIgualCostoTest() {
        // A -> B -> D y A -> C -> D cuestan lo mismo (2)
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C", "D");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("B", "D", new WeightedEdge(1));
        g.agregarArista("A", "C", new WeightedEdge(1));
        g.agregarArista("C", "D", new WeightedEdge(1));

        IDijkstraResult<String> res = algoritmos.dijkstra(g.construirComparable("A"), g);

        assertEquals(2.0, res.getCost("D"), 0.001);
        List<String> camino = res.getPath("D");
        // cualquiera de los dos caminos es correcto
        assertTrue(camino.equals(List.of("A", "B", "D")) || camino.equals(List.of("A", "C", "D")));
    }

    //  Floyd y Warshall

    @Test
    public void floydNoConectadosTest() {
        IFloydWarshallResult<String> res = algoritmos.floyd(grafo);

        // desde D no se llega a A
        assertEquals(Double.POSITIVE_INFINITY, res.getCost("D", "A"), 0.001);
        assertFalse(res.connected("D", "A"));
    }

    @Test
    public void floydGrafoVacioTest() {
        IFloydWarshallResult<String> res = algoritmos.floyd(nuevoGrafo());

        assertEquals(Double.POSITIVE_INFINITY, res.getCost("A", "B"), 0.001);
    }

    @Test
    public void warshallAlcanzabilidadTest() {
        IFloydWarshallResult<String> res = algoritmos.warshall(grafo);

        // A llega a D aunque no haya arista directa
        assertTrue(res.connected("A", "D"));
        // es dirigido: D no llega a A
        assertFalse(res.connected("D", "A"));
        assertTrue(res.connected("B", "B"));
    }

    @Test
    public void warshallGrafoSinAristasTest() {
        IFloydWarshallResult<String> res = algoritmos.warshall(nuevoGrafo("A", "B"));

        assertFalse(res.connected("A", "B"));
        assertFalse(res.connected("B", "A"));
    }

    //  Centro y excentricidad

    @Test
    public void centroDelGrafoTest() {
        // B, C y D llegan a A en un paso, asi que A es el centro
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C", "D");
        g.agregarArista("B", "A", new WeightedEdge(1));
        g.agregarArista("C", "A", new WeightedEdge(1));
        g.agregarArista("D", "A", new WeightedEdge(1));
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("B", "C", new WeightedEdge(1));
        g.agregarArista("C", "D", new WeightedEdge(1));

        assertEquals("A", algoritmos.obtenerCentroGrafo(g));
        assertEquals(1.0, algoritmos.obtenerExcentricidad(g, g.construirComparable("A")), 0.001);
    }

    @Test
    public void centroDeGrafoVacioTest() {
        assertNull(algoritmos.obtenerCentroGrafo(nuevoGrafo()));
    }

    @Test
    public void centroDeUnVerticeTest() {
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A");

        assertEquals("A", algoritmos.obtenerCentroGrafo(g));
        assertEquals(0.0, algoritmos.obtenerExcentricidad(g, g.construirComparable("A")), 0.001);
    }

    @Test
    public void excentricidadInfinitaSiNoTodosLleganTest() {
        // desde D no se llega a A
        assertEquals(Double.POSITIVE_INFINITY,
                algoritmos.obtenerExcentricidad(grafo, grafo.construirComparable("A")), 0.001);
    }

    @Test
    public void excentricidadDeVerticeInexistenteTest() {
        assertEquals(Double.POSITIVE_INFINITY,
                algoritmos.obtenerExcentricidad(grafo, grafo.construirComparable("Z")), 0.001);
    }

    //  Todos los caminos

    @Test
    public void todosLosCaminosTest() {
        // de A a D: A-B-C-D y A-C-D
        List<Path<String>> caminos = algoritmos.obtenerTodosLosCaminos(
                grafo.construirComparable("A"), grafo.construirComparable("D"), grafo);

        assertEquals(2, caminos.size());
    }

    @Test
    public void todosLosCaminosSinCaminoTest() {
        // desde D no se llega a A
        assertTrue(algoritmos.obtenerTodosLosCaminos(
                grafo.construirComparable("D"), grafo.construirComparable("A"), grafo).isEmpty());
    }

    @Test
    public void todosLosCaminosConCicloNoSeRepitenVerticesTest() {
        // el ciclo A -> B -> A no tiene que generar caminos infinitos
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("B", "A", new WeightedEdge(1));
        g.agregarArista("B", "C", new WeightedEdge(1));

        List<Path<String>> caminos = algoritmos.obtenerTodosLosCaminos(
                g.construirComparable("A"), g.construirComparable("C"), g);

        assertEquals(1, caminos.size());
        assertEquals(List.of("A", "B", "C"), caminos.get(0).getPath());
    }

    @Test
    public void todosLosCaminosVerticeInexistenteTest() {
        assertTrue(algoritmos.obtenerTodosLosCaminos(
                grafo.construirComparable("A"), grafo.construirComparable("Z"), grafo).isEmpty());
    }

    //  Recorridos

    @Test
    public void recorridoEnAmplitudPorNivelesTest() {
        // A -> B, A -> C, B -> D: primero A, despues B y C, por ultimo D
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C", "D");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("A", "C", new WeightedEdge(1));
        g.agregarArista("B", "D", new WeightedEdge(1));

        List<String> orden = new ArrayList<>();
        algoritmos.recorridoEnAmplitud(g, g.construirComparable("A"), orden::add);

        assertEquals(4, orden.size());
        assertEquals("A", orden.get(0));
        assertEquals("D", orden.get(3));
    }

    @Test
    public void recorridosNoConexoSoloVisitanLoAlcanzableTest() {
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C");
        g.agregarArista("A", "B", new WeightedEdge(1));

        List<String> bfs = new ArrayList<>();
        List<String> dfs = new ArrayList<>();
        algoritmos.recorridoEnAmplitud(g, g.construirComparable("A"), bfs::add);
        algoritmos.recorridoEnProfundidad(g, g.construirComparable("A"), dfs::add);

        assertEquals(List.of("A", "B"), bfs);
        assertEquals(List.of("A", "B"), dfs);
    }

    @Test
    public void recorridosDesdeVerticeInexistenteTest() {
        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnAmplitud(grafo, grafo.construirComparable("Z"), visitados::add);
        algoritmos.recorridoEnProfundidad(grafo, grafo.construirComparable("Z"), visitados::add);

        assertTrue(visitados.isEmpty());
    }

    @Test
    public void recorridoConCicloNoRepiteVerticesTest() {
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("B", "C", new WeightedEdge(1));
        g.agregarArista("C", "A", new WeightedEdge(1));

        List<String> visitados = new ArrayList<>();
        algoritmos.recorridoEnProfundidad(g, g.construirComparable("A"), visitados::add);

        assertEquals(3, visitados.size());
    }

    //  Clasificacion topologica

    @Test
    public void topologicaConCicloDevuelveVacioTest() {
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B");
        g.agregarArista("A", "B", new WeightedEdge(1));
        g.agregarArista("B", "A", new WeightedEdge(1));

        assertTrue(algoritmos.calcularClasificacionTopologica(g).isEmpty());
    }

    @Test
    public void topologicaGrafoVacioTest() {
        assertTrue(algoritmos.calcularClasificacionTopologica(nuevoGrafo()).isEmpty());
    }

    @Test
    public void topologicaRespetaLasDependenciasTest() {
        // A -> C y B -> C: A y B tienen que ir antes que C
        DirectedGraph<String, WeightedEdge> g = nuevoGrafo("A", "B", "C");
        g.agregarArista("A", "C", new WeightedEdge(1));
        g.agregarArista("B", "C", new WeightedEdge(1));

        List<String> orden = algoritmos.calcularClasificacionTopologica(g);

        assertEquals(3, orden.size());
        assertTrue(orden.indexOf("A") < orden.indexOf("C"));
        assertTrue(orden.indexOf("B") < orden.indexOf("C"));
    }
}
