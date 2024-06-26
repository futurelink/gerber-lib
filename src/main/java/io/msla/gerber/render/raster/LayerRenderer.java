package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import lombok.Getter;

import java.awt.*;
import java.awt.geom.Point2D;

@Getter
abstract public class LayerRenderer {
    private final Double scale;
    private final Point2D center;

    protected LayerRenderer(Double scale, Point2D center) {
        this.scale = scale;
        this.center = center;
    }

    abstract public void draw(Layer g, final Point2D offset, Color color);

    protected Point2D translatedPoint(Double x, Double y, Point2D offset) {
        if (offset == null) {
            return new Point2D.Double((x + center.getX()) / scale, -(y - center.getY()) / scale);
        } else {
            return new Point2D.Double((offset.getX() + x + center.getX()) / scale, -(offset.getY() + y - center.getY()) / scale);
        }
    }

    protected Point2D translatedPoint(Point2D p, Point2D offset) {
        return translatedPoint(p.getX(), p.getY(), offset);
    }

    protected Point2D translatedPoint(Double x, Double y) {
        return translatedPoint(x, y, null);
    }
}
