package io.msla.gerber.render.raster.tools;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
public class Pen {
    @Setter private Color color;
    private Double width;
    private Stroke stroke;

    public Pen() {
        this.color = Color.WHITE;
        this.setWidth(1.0);
    }

    public Pen(Color color, Double width) {
        this.color = color;
        this.setWidth(width);
    }

    public Pen(Pen pen) {
        this.color = pen.color;
        this.setWidth(pen.width);
    }

    public void setWidth(Double width) {
        this.width = width;
        this.stroke = new BasicStroke(this.width.intValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
}
