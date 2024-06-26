package io.msla.gerber.render.raster;

import io.msla.gerber.render.raster.tools.Brush;
import io.msla.gerber.render.raster.tools.Pen;
import io.msla.gerber.render.raster.tools.Polygon;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

public class GraphicsCanvas {
    private static final Logger logger = Logger.getLogger(GraphicsCanvas.class.getName());
    private final Graphics2D graphics;
    @Getter @Setter private Pen pen;
    @Getter @Setter private Brush brush;

    public GraphicsCanvas(Graphics2D graphics) {
        this.graphics = graphics;
        this.pen = new Pen();
        this.brush = new Brush();
    }

    void drawPolygon(Point2D[] points) {
        var poly = new java.awt.Polygon();
        for (var point : points) poly.addPoint((int) Math.round(point.getX()), (int) Math.round(point.getY()));

        if (brush != null) {
            logger.fine("Filled polygon of " + points.length + " points");
            graphics.setColor(brush.getColor());
            if (pen != null) graphics.setStroke(pen.getStroke());
            graphics.fillPolygon(poly);
        }

        if (pen != null) {
            logger.fine("Polygon of " + points.length + " points");
            graphics.setColor(pen.getColor());
            graphics.setStroke(pen.getStroke());
            graphics.drawPolygon(poly);
        }
    }

    void drawPolygon(Polygon polygon) {
        drawPolygon(polygon.getPoints());
    }

    void drawArc(Rectangle2D rect, Integer start, Integer end) {
        logger.fine("Arc " + rect.toString() + " - " + start + " to " + end);
        graphics.setColor(pen.getColor());
        graphics.setStroke(pen.getStroke());
        graphics.drawArc(
                (int) Math.round(rect.getX()), (int) Math.round(rect.getY()),
                (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()), start, end);
    }

    void drawLine(Point2D start, Point2D end) {
        logger.fine("Line " + start.toString() + " to " + end.toString());
        if (pen != null) {
            graphics.setColor(pen.getColor());
            graphics.setStroke(pen.getStroke());
            graphics.drawLine(
                    (int) Math.round(start.getX()), (int) Math.round(start.getY()),
                    (int) Math.round(end.getX()), (int) Math.round(end.getY()));
        }
    }

    void drawEllipse(Point2D center, Double radius1, Double radius2) {
        if (brush != null) {
            logger.fine("Filled ellipse color " + brush.getColor() + ": " + center.toString() + " - " + radius1 + " x " + radius2);
            graphics.setColor(brush.getColor());
            if (pen != null) graphics.setStroke(pen.getStroke());
            graphics.fillOval(
                    (int) Math.round(center.getX() - radius1),
                    (int) Math.round(center.getY() - radius2),
                    radius1.intValue() * 2, radius2.intValue() * 2);
        }

        if (pen != null) {
            logger.fine("Outline ellipse color " + pen.getColor() + ": " + center.toString() + " - " + radius1 + " x " + radius2);
            graphics.setColor(pen.getColor());
            graphics.setStroke(pen.getStroke());
            graphics.drawOval(
                    (int) Math.round(center.getX()), (int) Math.round(center.getY()),
                    radius1.intValue(), radius2.intValue());
        }
    }

    void drawRect(Rectangle2D rect) {
        if (brush != null) {
            logger.fine("Filled rect " + rect.toString());
            graphics.setColor(brush.getColor());
            if (pen != null) graphics.setStroke(pen.getStroke());
            graphics.fillRect(
                    (int) Math.round(rect.getX()), (int) Math.round(rect.getY()),
                    (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
        }

        if (pen != null) {
            logger.fine("Outline rect " + rect.toString());
            graphics.setColor(pen.getColor());
            graphics.setStroke(pen.getStroke());
            graphics.drawRect(
                    (int) Math.round(rect.getX()), (int) Math.round(rect.getY()),
                    (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
        }
    }

}

