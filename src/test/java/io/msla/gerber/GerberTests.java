package io.msla.gerber;

import io.msla.gerber.drl.ExcellonReader;
import io.msla.gerber.exceptions.GerberException;
import io.msla.gerber.gbr.GerberReader;
import io.msla.gerber.render.raster.BufferedImageRenderer;
import io.msla.gerber.render.raster.RenderException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GerberTests extends Common {

    @Test
    void testReadGerberFile() throws IOException, GerberException {
        var reader = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-B_Cu.gbr")));
        var gerber = reader.read("Copper Back Title");

        assertEquals(13, gerber.getApertures().size());
        assertEquals(4469, gerber.getContents().size());
        assertEquals(Layer.Type.BackCopper, gerber.getLayerType());
        assertEquals(28.955, (double) Math.round(gerber.getWidth() * 1000) / 1000);
        assertEquals(47.129, (double) Math.round(gerber.getHeight() * 1000) / 1000);
    }

    @Test
    void testReadAndRenderFile() throws IOException, GerberException, RenderException {
        var reader = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-B_Cu.gbr")));
        var gerber = reader.read("Copper Back Title");

        var drlReader = new ExcellonReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-PTH.drl")));
        var drl = drlReader.read("Top through holes");

        var renderer = new BufferedImageRenderer(0.02);
        renderer.render(gerber, Color.ORANGE);
        renderer.render(drl, Color.WHITE);

        var image = renderer.getImage();
        ImageIO.write(image, "png", new File("rendered_gerber.png"));
    }
}
