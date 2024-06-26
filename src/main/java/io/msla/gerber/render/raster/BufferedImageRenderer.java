package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import io.msla.gerber.drl.Excellon;
import io.msla.gerber.gbr.Gerber;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public final class BufferedImageRenderer {
    @Getter private BufferedImage image;
    private final int imageType;
    private final Double scale;
    private Point2D gerberCenter;

    /**
     * Add padding in millimeters around rendered image.
     * Default padding is 0.5mm.
     */
    @Setter private Double padding = 0.5;

    /**
     * Rendered image width.
     */
    @Getter private Integer width;

    /**
     * Rendered image height.
     */
    @Getter private Integer height;

    public BufferedImageRenderer(Double scale, int imageType) {
        this.scale = scale;
        this.imageType = imageType;
    }

    public BufferedImageRenderer(Double scale) {
        this(scale, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Renders layer data.
     * @param layer layer data. Either Gerber or Excellon DRL.
     * @param color color to draw geometry with.
     */
    public void render(Layer layer, Color color) throws RenderException {
        if (layer == null) throw new RenderException("Layer data can't be null");
        if (color == null) throw new RenderException("Color can't be null");

        // New size of image
        if (width == null && layer.getWidth() != null) width = (int) Math.round((layer.getWidth() + (padding * 2)) / scale);
        if (height == null && layer.getHeight() != null) height = (int) Math.round((layer.getHeight() + (padding * 2)) / scale);

        // Create final rendered image if not set
        if (this.image == null) {
            if (width == null || height == null) throw new RenderException("Can't render, dimensions are unknown");
            this.image = new BufferedImage(width, height, imageType);
        }

        // Create temporary image
        var tmpImage = new BufferedImage(width, height, imageType);
        var tmpGraphics = tmpImage.createGraphics();
        var canvas = new Graphics2DCanvas(tmpGraphics);

        tmpGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Handle Gerber data
        if (layer instanceof Gerber) {
            // Center offset is being taken from the first rendered Gerber layer,
            // usually it's edge cuts layer.
            if (gerberCenter == null) gerberCenter = new Point2D.Double(-layer.getMinX(), layer.getMaxY());

            var gerberData = new GerberData(layer);
            var gerberRenderer = new GerberRenderer(canvas, scale, gerberCenter);
            gerberRenderer.setColor(new Color(color.getRGB()));
            gerberRenderer.setApertures(gerberData.getApertures(layer.getLayerType()));
            gerberRenderer.setMacros(gerberData.getMacros(layer.getLayerType()));
            gerberRenderer.draw(layer, new Point2D.Double(padding, -padding));
        }

        // Handle Excellon DRL data
        else if (layer instanceof Excellon) {
            if (gerberCenter == null) throw new RenderException("Excellon DRL can only be rendered after Gerber layer");
            var drlRenderer = new ExcellonRenderer(canvas, scale, gerberCenter);
            drlRenderer.setColor(new Color(color.getRGB()));
            drlRenderer.draw(layer, new Point2D.Double(padding, -padding));
        }
        tmpGraphics.dispose();

        // Compose images
        var graphics = image.createGraphics();
        if (color.getAlpha() < 255) graphics.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, color.getAlpha() / 255.f));
        graphics.drawImage(tmpImage, 0, 0, null);
        graphics.dispose();
    }
}
