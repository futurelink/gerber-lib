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

import static org.junit.jupiter.api.Assertions.*;

public class GerberTests extends Common {

    @Test
    void testReadGerberFile() throws IOException, GerberException {
        var reader = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-B_Cu.gbr")));
        var gerber = reader.read("Copper Back Title");

        assertTrue(gerber.isHasGraphics());
        assertEquals(13, gerber.getApertures().size());
        assertEquals(4469, gerber.getContents().size());
        assertEquals(Layer.Type.BackCopper, gerber.getLayerType());
        assertEquals(47.129, (double) Math.round(gerber.getWidth() * 1000) / 1000);
        assertEquals(28.955, (double) Math.round(gerber.getHeight() * 1000) / 1000);
    }

    @Test
    void testReadNoGraphicsFile() throws IOException, GerberException {
        var reader = new GerberReader(new FileInputStream(resourceFile("gerbers/hldi-B_Paste.gbr")));
        var gerber = reader.read("Back paste title");
        assertEquals(2, gerber.getContents().size());
        assertFalse(gerber.isHasGraphics());
    }

    @Test
    void testReadAndRenderFile() throws IOException, GerberException, RenderException {
        var reader = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-Edge_Cuts.gbr")));
        var gerber = reader.read("Edge cuts layer title");
        assertTrue(gerber.isHasGraphics());

        var reader1 = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-B_Cu.gbr")));
        var gerber1 = reader1.read("Copper Back layer");
        assertTrue(gerber1.isHasGraphics());

        var reader2 = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-F_Cu.gbr")));
        var gerber2 = reader2.read("Copper Front layer");
        assertTrue(gerber2.isHasGraphics());

        var drlReader = new ExcellonReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-PTH.drl")));
        var drl = drlReader.read("Top through holes");
        assertTrue(drl.isHasGraphics());

        var renderer = new BufferedImageRenderer(0.02);
        renderer.setPadding(1.0); // Add 1mm padding around a render
        renderer.render(gerber, new Color(255, 255, 0, 170));
        renderer.render(gerber1, new Color(0, 255, 0, 170));
        renderer.render(gerber2, new Color(255, 0, 0, 170));
        renderer.render(drl, new Color(0,0, 0));

        var image = renderer.getImage();
        ImageIO.write(image, "png", new File("sample/rendered_gerber.png"));
    }

    @Test
    void testRenderRotateFile() throws IOException, GerberException, RenderException {
        var reader1 = new GerberReader(new FileInputStream(resourceFile("gerbers/tiny1616_dev_board-B_Cu.gbr")));
        var gerber1 = reader1.read("Copper Back layer");
        assertTrue(gerber1.isHasGraphics());

        var renderer = new BufferedImageRenderer(0.02);
        renderer.setPadding(1.0); // Add 1mm padding around a render
        renderer.setRotate(BufferedImageRenderer.Rotate.Rotate90CW);
        renderer.render(gerber1, new Color(0, 255, 0, 170));

        var image = renderer.getImage();
        ImageIO.write(image, "png", new File("sample/rendered_gerber_rotated.png"));
    }

    @Test
    void testRenderArcs0And360() throws IOException, GerberException, RenderException {
        var reader1 = new GerberReader(new FileInputStream(resourceFile("gerbers/hldi-Edge_Cuts.gbr")));
        var gerber1 = reader1.read("Edge cuts");
        assertTrue(gerber1.isHasGraphics());

        var renderer = new BufferedImageRenderer(0.02);
        renderer.setPadding(1.0); // Add 1mm padding around a render
        renderer.render(gerber1, new Color(0, 255, 0, 170));

        var image = renderer.getImage();
        ImageIO.write(image, "png", new File("sample/rendered_edge_cuts.png"));
    }
}
