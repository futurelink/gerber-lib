package io.msla.gerber.render.raster;

import io.msla.gerber.render.raster.tools.Brush;
import io.msla.gerber.render.raster.tools.Pen;
import io.msla.gerber.render.raster.tools.Polygon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Gerber and Excellon rendering canvas interface.
 */
public interface RenderCanvas {
    Pen getPen();
    void setPen(Pen pen);

    Brush getBrush();
    void setBrush(Brush brush);

    void drawPolygon(Point2D[] points);
    void drawPolygon(Polygon polygon);
    void drawArc(Rectangle2D rect, Double angleStart, Double angleSpan);
    void drawLine(Point2D start, Point2D end);
    void drawEllipse(Point2D center, Double radius1, Double radius2);
    void drawRect(Rectangle2D rect);
}
