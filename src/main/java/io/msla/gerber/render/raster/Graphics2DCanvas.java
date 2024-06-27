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

public class Graphics2DCanvas implements RenderCanvas {
    private static final Logger logger = Logger.getLogger(Graphics2DCanvas.class.getName());
    private final Graphics2D graphics;
    @Getter @Setter private Pen pen;
    @Getter @Setter private Brush brush;

    public Graphics2DCanvas(Graphics2D graphics) {
        this.graphics = graphics;
        this.pen = new Pen();
        this.brush = new Brush();
    }

    public void drawPolygon(Point2D[] points) {
        if (brush == null && pen == null) {
            logger.warning("Polygon should have been drawn, but neither pen no brush set");
            return;
        }

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

    public void drawPolygon(Polygon polygon) {
        drawPolygon(polygon.getPoints());
    }

    public void drawArc(Rectangle2D rect, Double angleStart, Double angleSpan) {
        var angleStartDeg = (int) Math.round(angleStart / Math.PI * 180.0);
        var angleSpanDeg = (int) Math.round(angleSpan / Math.PI * 180.0);
        if (pen != null) {
            if (angleSpanDeg != 0) {
                logger.fine("Arc " + rect.toString() + " - " +
                        angleStart + " (" + angleStartDeg + " deg) spans " +
                        angleSpan + " (" + angleSpanDeg + " deg)");
                graphics.setColor(pen.getColor());
                graphics.setStroke(pen.getStroke());
                graphics.drawArc(
                        (int) Math.round(rect.getX()), (int) Math.round(rect.getY()),
                        (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()),
                        angleStartDeg, angleSpanDeg);
            } else logger.warning("Arc should have been drawn, but angle span is zero");
        } else logger.warning("Arc should have been drawn, but no pen set");
    }

    public void drawLine(Point2D start, Point2D end) {
        if (pen != null) {
            logger.fine("Line " + start.toString() + " to " + end.toString());
            graphics.setColor(pen.getColor());
            graphics.setStroke(pen.getStroke());
            graphics.drawLine(
                    (int) Math.round(start.getX()), (int) Math.round(start.getY()),
                    (int) Math.round(end.getX()), (int) Math.round(end.getY()));
        } else logger.warning("Line should have been drawn, but no pen set");
    }

    public void drawEllipse(Point2D center, Double radius1, Double radius2) {
        if (brush == null && pen == null) {
            logger.warning("Ellipse should have been drawn, but neither pen no brush set");
            return;
        }

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

    public void drawRect(Rectangle2D rect) {
        if (brush == null && pen == null) {
            logger.warning("Rectangle should have been drawn, but neither pen no brush set");
            return;
        }

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

