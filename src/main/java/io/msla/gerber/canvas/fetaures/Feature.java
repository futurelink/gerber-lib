package io.msla.gerber.canvas.fetaures;

import lombok.Getter;
import io.msla.gerber.Layer;
import io.msla.gerber.canvas.Geometry;
import io.msla.gerber.drl.holes.Hole;
import io.msla.gerber.canvas.Point;
import io.msla.gerber.canvas.Range;

import java.util.*;

abstract public class Feature {
    @Getter private final UUID id;
    private final ArrayList<Geometry> affectedGeometry;
    @Getter private final HashMap<Geometry, List<Range>> pierces;
    @Getter protected Point topLeft;
    @Getter protected Point bottomRight;

    public Feature(UUID id, Point topLeft, Point bottomRight) {
        this.id = id;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.affectedGeometry = new ArrayList<>();
        this.pierces = new HashMap<>();
    }

    public Iterator<Geometry> affectedGeometry() { return affectedGeometry.iterator(); }

    protected void addAffectedGeometry(Geometry g) {
        affectedGeometry.add(g);
    }

    protected void addPiercing(Geometry g, Range r) {
        if (!pierces.containsKey(g)) pierces.put(g, new ArrayList<>());
        pierces.get(g).add(r);
    }

    public void moveOffset(double x, double y) {
        this.topLeft = new Point(topLeft.getX() + x, topLeft.getY() + y);
        this.bottomRight = new Point(bottomRight.getX() + x, bottomRight.getY() + x);
    }

    abstract public void clean();
    abstract public boolean affects(Geometry g);
    abstract public void cleanAffectedGeometry(Layer.Type type);
    abstract public void calculateAffectedGeometry(Layer.Type type, Geometry g);
    abstract public Set<Layer.Type> affectedLayerTypes();
    abstract public boolean isValid();
    abstract public Iterator<Geometry> buildGeometry();
    abstract public Iterator<Hole> buildHoles();
}
