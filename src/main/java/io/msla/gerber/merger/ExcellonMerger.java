package io.msla.gerber.merger;

import io.msla.gerber.drl.Excellon;
import lombok.Getter;
import io.msla.gerber.Layer;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class ExcellonMerger extends Merger {
    private final static Logger log = Logger.getLogger(ExcellonMerger.class.getName());
    private final Excellon layer;

    public ExcellonMerger(Layer.Type type, String name) {
        layer = new Excellon(name);
    }

    @Override
    public final void add(Layer source, double xOffset, double yOffset) {
        if (source instanceof Excellon e) {
            log.log(Level.INFO, "Adding Excellon file {0}", new Object[]{source.getName()});
            var holes = e.holes();
            while (holes.hasNext()) {
                layer.addHole(holes.next().offset(xOffset, yOffset));
            }
        }
    }

    @Override
    public void clean() {
        layer.clean();
    }
}
