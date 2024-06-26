package io.msla.gerber.drl.holes;

import lombok.Getter;
import io.msla.gerber.canvas.Point;

@Getter
abstract public class Hole extends Point {
    private final Double diameter;

    public Hole(Point center, Double diameter) {
        this(center.getX(), center.getY(), diameter);
    }

    public Hole(double x, double y, Double diameter) {
        super(x, y);
        this.diameter = diameter;
    }

    abstract public Hole offset(double xOffset, double yOffset);
}
