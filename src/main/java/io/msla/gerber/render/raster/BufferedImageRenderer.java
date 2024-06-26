package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import io.msla.gerber.drl.Excellon;
import io.msla.gerber.gbr.Gerber;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class BufferedImageRenderer {
    @Getter private BufferedImage image;
    private final Double scale;
    private Point2D gerberCenter;

    public BufferedImageRenderer(Double scale) {
        this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        this.scale = scale;
    }

    public void render(Layer layer, Color color) throws RenderException {
        if ((layer.getWidth() == null) || layer.getHeight() == null) {
            System.out.println("Rendering on an exising canvas as layer dimensions are unknown");
        } else {
            this.image = new BufferedImage(
                    (int) (layer.getWidth() / scale),
                    (int) (layer.getHeight() / scale),
                    BufferedImage.TYPE_INT_RGB);
        }

        var canvas = new GraphicsCanvas((Graphics2D) this.image.getGraphics());

        if (layer instanceof Gerber) {
            gerberCenter = new Point2D.Double(-layer.getMinX(), layer.getMaxY());
            var gerberData = new GerberData(layer);
            var gerberRenderer = new GerberRenderer(canvas, scale,
                    new Point2D.Double(-layer.getMinX(), layer.getMaxY())
            );
            gerberRenderer.setApertures(gerberData.getApertures(layer.getLayerType()));
            gerberRenderer.setMacros(gerberData.getMacros(layer.getLayerType()));
            gerberRenderer.draw(layer, new Point2D.Double(0, 0), color);
        }

        else if (layer instanceof Excellon) {
            if (gerberCenter == null) throw new RenderException("Excellon DRL can only be rendered after Gerber layer");
            var drlRenderer = new ExcellonRenderer(canvas, scale, gerberCenter);
            drlRenderer.draw(layer, new Point2D.Double(0, 0), color);
        }
    }
}
