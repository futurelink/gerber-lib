package io.msla.gerber.merger;

import io.msla.gerber.Layer;

abstract public class Merger {
    public abstract Layer getLayer();
    public abstract void clean();
    public abstract void add(Layer layer, double xOffset, double yOffset) throws MergerException;
}
