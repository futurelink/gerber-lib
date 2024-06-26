package io.msla.gerber.canvas.fetaures;

import lombok.Getter;
import io.msla.gerber.canvas.Point;

import java.util.UUID;

@Getter
abstract public class RoundFeature extends Feature {
    private Point center;
    private final double radius;
    public RoundFeature(UUID id, Point center, double radius){
        super(id,
            new Point(center.getX() - radius, center.getY() - radius),
            new Point(center.getX() + radius, center.getY() + radius));
        this.center = center;
        this.radius = radius;
    }

    public void moveOffset(double x, double y) {
        this.center = center.offset(x, y);
        super.moveOffset(x, y);
        clean();
    }
}
