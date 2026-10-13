package grafo.model.result;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    @Test
    public void Pathtest() {
        Path<String> camino = new Path<>(List.of("A", "B", "C"), 10.0);

        assertEquals(3, camino.getPath().size());
        assertEquals(10.0, camino.getCost());
    }
}
