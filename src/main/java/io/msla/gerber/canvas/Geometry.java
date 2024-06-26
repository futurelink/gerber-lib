package io.msla.gerber.canvas;

import lombok.Getter;

@Getter
public abstract class Geometry extends Range {
    public enum Interpolation { LINEAR, CW, CCW }
    public enum QuadrantMode { SINGLE, MULTI }
    private final int aperture;
    private final Interpolation interpolation;

    public Geometry(Point start, Point end, Interpolation interpolation, int aperture){
        super(start, end);
        this.aperture = aperture;
        this.interpolation = interpolation;
    }

    public static Interpolation interpolationByCode(int code) {
        return switch (code) {
            case 1 -> Interpolation.LINEAR;
            case 2 -> Interpolation.CW;
            case 3 -> Interpolation.CCW;
            default -> null;
        };
    }
}
