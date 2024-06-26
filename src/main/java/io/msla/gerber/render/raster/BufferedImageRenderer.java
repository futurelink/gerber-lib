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
    private Double width;
    private Double height;

    public BufferedImageRenderer(Double scale) {
        this.scale = scale;
    }

    public void render(Layer layer, Color color) throws RenderException {
        // New size of image
        if (width == null || (layer.getWidth() != null && width < layer.getWidth() / scale)) width = layer.getWidth() / scale;
        if (height == null || (layer.getHeight() != null && height < layer.getWidth() / scale)) height = layer.getHeight() / scale;

        // Create final rendered image if not set
        if (this.image == null) {
            if (width == null || height == null) throw new RenderException("Can't render, dimensions are unknown");
            this.image = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_ARGB);
        }

        // Create temporary image
        var tmpImage = new BufferedImage(width.intValue(), height.intValue(), BufferedImage.TYPE_INT_ARGB);
        var tmpGraphics = tmpImage.createGraphics();
        var canvas = new GraphicsCanvas(tmpGraphics);

        tmpGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (layer instanceof Gerber) {
            gerberCenter = new Point2D.Double(-layer.getMinX(), layer.getMaxY());
            var gerberData = new GerberData(layer);
            var gerberRenderer = new GerberRenderer(canvas, scale,
                    new Point2D.Double(-layer.getMinX(), layer.getMaxY())
            );
            gerberRenderer.setColor(new Color(color.getRGB()));
            gerberRenderer.setApertures(gerberData.getApertures(layer.getLayerType()));
            gerberRenderer.setMacros(gerberData.getMacros(layer.getLayerType()));
            gerberRenderer.draw(layer, new Point2D.Double(0, 0));
        }

        else if (layer instanceof Excellon) {
            if (gerberCenter == null) throw new RenderException("Excellon DRL can only be rendered after Gerber layer");
            var drlRenderer = new ExcellonRenderer(canvas, scale, gerberCenter);
            drlRenderer.setColor(new Color(color.getRGB()));
            drlRenderer.draw(layer, new Point2D.Double(0, 0));
        }
        tmpGraphics.dispose();

        // Compose images
        var graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, color.getAlpha() / 255.f));
        graphics.drawImage(tmpImage, 0, 0, null);
        graphics.dispose();
    }
}
