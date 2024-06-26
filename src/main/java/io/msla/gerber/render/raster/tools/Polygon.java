package io.msla.gerber.render.raster.tools;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final List<Point2D> points = new ArrayList<>();

    public void append(Point2D point) {
        points.add(point);
    }

    public Point2D[] getPoints() {
        return points.toArray(Point2D[]::new);
    }
}
