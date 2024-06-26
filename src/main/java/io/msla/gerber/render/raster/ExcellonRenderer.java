package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import io.msla.gerber.drl.Excellon;
import io.msla.gerber.drl.holes.HoleRound;
import io.msla.gerber.drl.holes.HoleRouted;
import io.msla.gerber.render.raster.tools.Brush;
import io.msla.gerber.render.raster.tools.Pen;

import java.awt.geom.Point2D;

public final class ExcellonRenderer extends LayerRenderer {
    private final GraphicsCanvas canvas;

    public ExcellonRenderer(GraphicsCanvas canvas, Double scale, Point2D center) {
        super(scale, center);
        this.canvas = canvas;
    }

    @Override
    public void draw(Layer layer, Point2D offset) {
        if (layer instanceof Excellon e) {
            var pen = new Pen(getColor(), 1.0);
            var hi = e.holes();
            while (hi.hasNext()) {
                var h = hi.next();
                var c = translatedPoint(h.getX(), h.getY(), offset);
                var radius = h.getDiameter() / getScale() / 2;
                if (h instanceof HoleRouted r) {
                    pen.setWidth(radius * 2);
                    canvas.setPen(pen);
                    var iter = r.points();
                    while (iter.hasNext()) {
                        var p = iter.next();
                        var end = translatedPoint(p.getX(), p.getY(), offset);
                        canvas.drawLine(c, end);
                        c = end;
                    }
                    canvas.setPen(null);
                } else if (h instanceof HoleRound) {
                    canvas.setPen(null);
                    canvas.setBrush(new Brush(pen.getColor()));
                    canvas.drawEllipse(c, radius, radius);
                    canvas.setBrush(null);
                }
            }
        }
    }
}
