package io.msla.gerber.canvas;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Aperture {
    private final String macro;
    private final ArrayList<Double> measures;

    public Aperture(String macro, String measures) {
        this.macro = macro;
        this.measures = new ArrayList<>();
        for (var m : measures.split("X")) {
            this.measures.add(Double.parseDouble(m.trim()));
        }
    }
}
