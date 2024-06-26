package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import io.msla.gerber.canvas.Aperture;
import io.msla.gerber.canvas.Geometry;
import io.msla.gerber.canvas.Macro;
import io.msla.gerber.gbr.Gerber;
import io.msla.gerber.gbr.cmd.d.D01To03;
import io.msla.gerber.gbr.cmd.d.DAperture;
import io.msla.gerber.gbr.cmd.g.GCode;
import io.msla.gerber.render.raster.tools.Brush;
import io.msla.gerber.render.raster.tools.Pen;
import io.msla.gerber.render.raster.tools.Polygon;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
public final class GerberRenderer extends LayerRenderer {
    private final GraphicsCanvas canvas;
    @Setter private HashMap<Integer, Aperture> apertures;
    @Setter private HashMap<String, Macro> macros;

    private final static double arcQ = 2880 / Math.PI;

    static class ArcCache {
        private Point2D center;
        private double radius;
        private double angStart;
        private double angSpan;
    }

    private final HashMap<D01To03, ArcCache> arcCache;

    public GerberRenderer(GraphicsCanvas canvas, Double scale, Point2D center) {
        super(scale, center);
        this.canvas = canvas;
        this.arcCache = new HashMap<>();
    }

    @Override
    public void draw(Layer layer, final Point2D offset) {
        Aperture currentAperture = null;
        var currentInterpolation = Geometry.Interpolation.LINEAR;
        var currentPoint = new Point2D.Double(0, 0);
        var polygonMode = false;
        var polygonPoints = new ArrayList<Point2D>();
        var brush = new Brush(getColor());
        var pen = new Pen(getColor(), 1.0);

        canvas.setPen(null);
        canvas.setBrush(null);

        if (layer instanceof Gerber g) {
            for (var cmd : g.getContents()) {
                if (cmd instanceof D01To03 d) {
                    var p = new Point2D.Double(d.getX(), d.getY());
                    switch (d.getCode()) {
                        case 1:
                            if (currentInterpolation == Geometry.Interpolation.LINEAR) {
                                if (polygonMode) {
                                    polygonPoints.add(translatedPoint(p, offset));
                                } else {
                                    canvas.setPen(pen);
                                    drawApertureLine(translatedPoint(currentPoint, offset), translatedPoint(p, offset), currentAperture);
                                    canvas.setPen(null);
                                }
                            } else {
                                canvas.setPen(pen);
                                drawApertureArc(currentPoint, currentInterpolation, offset, currentAperture, d);
                                canvas.setPen(null);
                            }
                            break;
                        case 3:
                            if (apertures != null) {
                                canvas.setBrush(brush);
                                drawAperture(translatedPoint(p, offset), currentAperture, macros);
                                canvas.setBrush(null);
                            }
                            break;
                        default:
                            break;
                    }
                    currentPoint = p;
                } else if (cmd instanceof GCode gcode) {
                    if (gcode.getCode() <= 3) {
                        currentInterpolation = switch (gcode.getCode()) {
                            case 1 -> Geometry.Interpolation.LINEAR;
                            case 2 -> Geometry.Interpolation.CW;
                            case 3 -> Geometry.Interpolation.CCW;
                            default -> null;
                        };
                    } else if (gcode.getCode() == 36) {
                        polygonPoints.clear();
                        polygonMode = true;
                    } else if (gcode.getCode() == 37) {
                        if (polygonMode) {
                            canvas.setBrush(brush);
                            canvas.drawPolygon(polygonPoints.toArray(Point2D[]::new));
                            canvas.setBrush(null);
                            polygonMode = false;
                        }
                    }
                } else if ((apertures != null) && (cmd instanceof DAperture a)) {
                    currentAperture = apertures.get(a.getCode());
                }
            }
        }
    }

    public void drawApertureArc(
            Point2D currentPoint,
            Geometry.Interpolation interpolation,
            Point2D offset,
            Aperture aperture,
            D01To03 d)
    {
        ArcCache c;
        //if (arcCache.containsKey(d)) {
        //c = arcCache.get(d);
        //} else {
        c = new ArcCache();
        c.center = new Point2D.Double(currentPoint.getX() + d.getI(), currentPoint.getY() + d.getJ());
        c.radius = Math.sqrt(Math.pow(currentPoint.getX() - c.center.getX(), 2) + Math.pow(currentPoint.getY() - c.center.getY(), 2));
        c.angStart = Math.atan2(currentPoint.getY() - c.center.getY(), currentPoint.getX() - c.center.getX());
        var ang2 = Math.atan2(d.getY() - c.center.getY(), d.getX() - c.center.getX());
        if ((interpolation == Geometry.Interpolation.CCW) && (ang2 < 0)) ang2 = ang2 + 2 * Math.PI;
        c.angSpan = ang2 - c.angStart;
        arcCache.put(d, c);
        //}

        var arcRect = new Rectangle2D.Double(
                (c.center.getX() - c.radius + getCenter().getX() + ((offset != null) ? offset.getX() : 0)) / getScale(),
                -(c.center.getY() - c.radius - getCenter().getY() + ((offset != null) ? offset.getY() : 0)) / getScale(),
                c.radius * 2 / getScale(), -c.radius * 2 / getScale());

        var pen = new Pen(canvas.getPen());
        if (aperture != null) pen.setWidth(aperture.getMeasures().get(0) / getScale());
        canvas.setPen(pen);
        canvas.drawArc(arcRect, (int) (c.angStart * arcQ), (int) (c.angSpan * arcQ));
        canvas.setPen(null);
    }

    public void drawApertureLine(final Point2D start, final Point2D end, final Aperture aperture) {
        var pen = new Pen(canvas.getPen());
        if (aperture != null) pen.setWidth(aperture.getMeasures().get(0) / getScale());
        canvas.setPen(pen);
        canvas.drawLine(start, end);
    }

    public void drawAperture(final Point2D p, final Aperture a, final HashMap<String, Macro> macros) {
        if (a == null) return;

        switch (a.getMacro()) {
            case "C" -> {   // Circle
                var radius = a.getMeasures().get(0) / getScale() / 2;
                canvas.drawEllipse(p, radius, radius);
            }
            case "O" ->     // Oval
                    canvas.drawEllipse(p, a.getMeasures().get(0) / getScale() / 2, a.getMeasures().get(1) / getScale() / 2);

            case "R" -> {   // Rectangle
                var rx = a.getMeasures().get(0) / getScale() / 2;
                var ry = a.getMeasures().get(1) / getScale() / 2;
                canvas.drawRect(new Rectangle2D.Double(p.getX() - rx, p.getY() - ry, rx * 2, ry * 2));
            }
            default ->      // Macro name
                    drawMacro(p, macros.get(a.getMacro()), a.getMeasures().toArray(Double[]::new), canvas.getBrush().getColor());
        }
    }

    public void drawMacro(final Point2D p, Macro macro, Double[] measures, Color color) {
        var result = macro.eval(measures);
        for (var r : result) {
            switch (r.getType()) {
                case Circle -> {
                    var dia = r.getValue(0) / getScale() / 2;
                    var center = addPoints(
                            new Point2D.Double(
                                    r.getValue(1) / getScale(),
                                    -r.getValue(2) / getScale()),
                            p);
                    canvas.drawEllipse(center, dia, dia);
                }
                case Outline -> {
                    var poly = new Polygon();
                    for (var i = 0; i < r.getValue(0) * 2; i += 2) {
                        poly.append(addPoints(
                                new Point2D.Double(
                                        r.getValue(i+1) / getScale(),
                                        -r.getValue(i+2) / getScale()),
                                p));
                    }
                    canvas.drawPolygon(poly);
                }
                case VectorLine -> {
                    var prevPen = canvas.getPen();
                    canvas.setPen(new Pen(color, r.getValue(0) / getScale()));
                    canvas.drawLine(
                            addPoints(new Point2D.Double(r.getValue(1) / getScale(), -r.getValue(2) / getScale()), p),
                            addPoints(new Point2D.Double(r.getValue(3) / getScale(), -r.getValue(4) / getScale()), p));
                    canvas.setPen(prevPen);
                }
            }
        }
    }

    private Point2D addPoints(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }
}
