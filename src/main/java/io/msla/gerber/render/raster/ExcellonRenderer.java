package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import io.msla.gerber.drl.Excellon;
import io.msla.gerber.drl.holes.HoleRound;
import io.msla.gerber.drl.holes.HoleRouted;
import io.msla.gerber.render.raster.tools.Brush;
import io.msla.gerber.render.raster.tools.Pen;

import java.awt.geom.Point2D;

public final class ExcellonRenderer extends LayerRenderer {

    public ExcellonRenderer(RenderCanvas canvas, Double scale, Point2D center) {
        super(canvas, scale, center);
    }

    @Override
    public void draw(Layer layer, Point2D offset) {
        // Has no graphics, nothing to render
        if (!layer.isHasGraphics()) return;

        if (layer instanceof Excellon e) {
            var pen = new Pen(getColor(), 1.0);
            var hi = e.holes();
            while (hi.hasNext()) {
                var h = hi.next();
                var c = translatedPoint(h.getX(), h.getY(), offset);
                var radius = h.getDiameter() / getScale() / 2;
                if (h instanceof HoleRouted r) {
                    pen.setWidth(radius * 2);
                    getCanvas().setPen(pen);
                    var iter = r.points();
                    while (iter.hasNext()) {
                        var p = iter.next();
                        var end = translatedPoint(p.getX(), p.getY(), offset);
                        getCanvas().drawLine(c, end);
                        c = end;
                    }
                    getCanvas().setPen(null);
                } else if (h instanceof HoleRound) {
                    getCanvas().setPen(null);
                    getCanvas().setBrush(new Brush(pen.getColor()));
                    getCanvas().drawEllipse(c, radius, radius);
                    getCanvas().setBrush(null);
                }
            }
        }
    }
}
