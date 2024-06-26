package io.msla.gerber.gbr;

import io.msla.gerber.Layer;
import io.msla.gerber.gbr.cmd.Command;
import io.msla.gerber.gbr.cmd.FS;
import io.msla.gerber.gbr.cmd.MO;
import io.msla.gerber.gbr.cmd.a.AD;
import io.msla.gerber.gbr.cmd.a.AM;
import io.msla.gerber.gbr.cmd.d.D01To03;
import io.msla.gerber.gbr.cmd.d.DAperture;
import io.msla.gerber.gbr.cmd.g.GCode;
import io.msla.gerber.gbr.cmd.l.LM;
import io.msla.gerber.gbr.cmd.l.LP;
import io.msla.gerber.gbr.cmd.l.LR;
import io.msla.gerber.gbr.cmd.l.LS;
import io.msla.gerber.gbr.cmd.t.TF;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Gerber extends Layer {
    private Integer MOIndex = -1;
    private Integer FSIndex = -1;

    private final ArrayList<Command> Commands;

    public Gerber(String name) {
        super(name);
        this.Commands = new ArrayList<>();
    }

    public void add(Command cmd) {
        if (cmd instanceof MO) {
            MOIndex = Commands.size();
        } else if (cmd instanceof FS) {
            FSIndex = Commands.size();
        } else if (cmd instanceof D01To03 d) {
            // Update min and max coords
            if (d.getX() < minX) minX = d.getX();
            if (d.getY() < minY) minY = d.getY();
            if (d.getX() > maxX) maxX = d.getX();
            if (d.getY() > maxY) maxY = d.getY();
            setHasGraphics(true);
        }
        Commands.add(cmd);
    }

    @Override public Double getWidth() { return Math.abs(maxX - minX); }
    @Override public Double getHeight() { return Math.abs(maxY - minY); }

    public List<Command> getApertures() {
        Predicate<Command> f = cmd -> cmd instanceof AD;
        return Commands.stream().filter(f).toList();
    }

    public List<Command> getMacros() {
        Predicate<Command> f = cmd -> cmd instanceof AM;
        return Commands.stream().filter(f).toList();
    }

    public boolean hasMacro(String name) {
        for (var c : getMacros()) {
            if (((AM)c).getName().equals(name)) return true;
        }
        return false;
    }

    public List<Command> getHeader() {
        Predicate<Command> f = cmd ->
                (cmd instanceof TF) ||
                (cmd instanceof FS) ||
                (cmd instanceof MO);
        return Commands.stream().filter(f).toList();
    }

    public List<Command> getContents() {
        Predicate<Command> f = cmd -> {
            if (cmd instanceof GCode c) {
                return (c.getCode() != 4);  // Ignore any comments
            } else return
                    (cmd instanceof DAperture) ||
                    (cmd instanceof LP) ||
                    (cmd instanceof LM) ||
                    (cmd instanceof LR) ||
                    (cmd instanceof LS);
        };
        return Commands.stream().filter(f).toList();
    }

    public final int getApertureLastIndex() {
        int lastIndex = 10;
        for (var aperture : getApertures()) {
            if (((AD)aperture).getCode() > lastIndex) {
                lastIndex = ((AD)aperture).getCode();
            }
        }
        return lastIndex;
    }

    public final String getTF(String attributeName) {
        for (var cmd : getHeader()) {
            if (cmd instanceof TF c) {
                if (c.getName().equals(attributeName)) {
                    return String.join(",", c.getParams());
                }
            }
        }
        return null;
    }

    public final FS getFS() {
        if (FSIndex >= 0) {
            return (FS) Commands.get(FSIndex);
        }
        return null;
    }

    public final MO getMO() {
        if (MOIndex >= 0) {
            return (MO) Commands.get(MOIndex);
        }
        return null;
    }

    @Override
    public final Type getLayerType() {
        var fileFunction = getTF(TF.FileFunction);
        if (fileFunction == null) return null;

        var top = fileFunction.contains("Top");
        var bottom = fileFunction.contains("Bot");
        if (fileFunction.contains("Copper")) {
            return top ? Type.FrontCopper : Type.BackCopper;
        } else if (fileFunction.contains("Soldermask")) {
            return top ? Type.FrontMask : Type.BackMask;
        } else if (fileFunction.contains("Paste")) {
            return top ? Type.FrontPaste : Type.BackPaste;
        } else if (fileFunction.contains("Legend")) {
            return top ? Type.FrontSilk : Type.BackSilk;
        } else if (fileFunction.contains("Profile")) {
            return Type.EdgeCuts;
        }
        return null;
    }

    @Override
    public final void clean() {
        super.clean();
        MOIndex = FSIndex = -1;
        Commands.clear();
    }
}
