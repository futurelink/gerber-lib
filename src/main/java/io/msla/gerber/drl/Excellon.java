package io.msla.gerber.drl;

import io.msla.gerber.Layer;
import io.msla.gerber.drl.holes.Hole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excellon extends Layer {
    private final ArrayList<Hole> holes;

    public Excellon(String name) {
        super(name);
        this.holes = new ArrayList<>();
    }

    public final void addHole(Hole h) {
        holes.add(h);
        setHasGraphics(true);
    }

    public final Iterator<? extends Hole> holes() {
        return holes.iterator();
    }

    public final List<? extends Hole> holesOfDiameter(Double diameter) {
        return holes.stream().filter(a -> (a.getDiameter().equals(diameter))).toList();
    }

    public final List<Double> getDiameters() {
        var diameters = new ArrayList<Double>();
        for (var h : holes) {
            if (!diameters.contains(h.getDiameter())) diameters.add(h.getDiameter());
        }
        return diameters;
    }

    @Override public final Type getLayerType() {
        return Type.TopDrill;
    }
    @Override public Double getWidth() { return null; }
    @Override public Double getHeight() { return null; }

    @Override
    public final void clean() {
        super.clean();
        holes.clear();
    }
}
