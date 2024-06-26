package io.msla.gerber;

import io.msla.gerber.drl.ExcellonReader;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcellonTests extends Common {
    @Test
    void testReadDRLFile() throws IOException {
        var reader = new ExcellonReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-PTH.drl")));
        var drl = reader.read("Top drill");

        // Check diameters are in place
        var diameters = drl.getDiameters();
        assertTrue(diameters.contains(0.3));
        assertTrue(diameters.contains(0.4));
        assertTrue(diameters.contains(0.6));
        assertTrue(diameters.contains(1.0));
    }
}