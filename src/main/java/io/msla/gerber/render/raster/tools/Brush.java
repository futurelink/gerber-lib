package io.msla.gerber.render.raster.tools;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter @Getter
public class Brush {
    private Color color;

    public Brush() {
        this(Color.WHITE);
    }

    public Brush(Brush brush) {
        this.color = brush.getColor();
    }

    public Brush(Color color) {
        this.color = color;
    }
}
