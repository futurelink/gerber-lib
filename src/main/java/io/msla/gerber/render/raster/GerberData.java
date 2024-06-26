package io.msla.gerber.render.raster;

import io.msla.gerber.Layer;
import io.msla.gerber.canvas.Aperture;
import io.msla.gerber.canvas.Macro;
import io.msla.gerber.gbr.Gerber;
import io.msla.gerber.gbr.cmd.a.AD;
import io.msla.gerber.gbr.cmd.a.AM;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class GerberData {
    @Getter private final Layer layer;
    private final HashMap<Layer.Type, HashMap<Integer, Aperture>> apertures;
    private final HashMap<Layer.Type, HashMap<String, Macro>> macros;

    public GerberData(Layer m) {
        this.layer = m;
        this.apertures = new HashMap<>();
        this.macros = new HashMap<>();

        loadApertures(layer.getLayerType());
        loadMacros(layer.getLayerType());
    }

    public HashMap<Integer, Aperture> getApertures(Layer.Type type) { return apertures.get(type); }
    public HashMap<String, Macro> getMacros(Layer.Type type) {
        return macros.get(type);
    }

    public void loadApertures(Layer.Type type) {
        if (layer instanceof Gerber g) {
            apertures.put(type, new HashMap<>());
            for (var cmd : g.getApertures()) {
                if (cmd instanceof AD a)
                    apertures.get(type).put(a.getCode(), new Aperture(a.getMacro(), a.getValue()));
            }
        }
    }

    public void loadMacros(Layer.Type type) {
        if (layer instanceof Gerber g) {
            macros.put(type, new HashMap<>());
            for (var cmd : g.getMacros()) {
                if (cmd instanceof AM a) {
                    var t = new ArrayList<String>();
                    a.blocks().forEachRemaining(t::add);
                    macros.get(type).put(a.getName(), new Macro(t));
                }
            }
        }
    }

    public void clear() {
        apertures.clear();
        macros.clear();
    }
}
