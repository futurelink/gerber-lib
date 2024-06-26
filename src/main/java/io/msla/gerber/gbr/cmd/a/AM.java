package io.msla.gerber.gbr.cmd.a;

import io.msla.gerber.gbr.cmd.CommandNamed;

import java.util.ArrayList;
import java.util.Iterator;

public class AM extends CommandNamed {
    private final ArrayList<String> Blocks;

    private AM(String name) {
        super(name);
        this.Blocks = new ArrayList<>();
    }

    private void addBlock(String block) {
        this.Blocks.add(block);
    }

    public Iterator<String> blocks() {
        return Blocks.iterator();
    }

    static public AM fromString(String str) {
        var t = str.substring(2).replace("\n", "");
        var blocks = t.split("\\*");
        var am = new AM(blocks[0]);
        for (int i = 1; i < blocks.length; i++) {
            am.addBlock(blocks[i]);
        }
        return am;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("%")
                .append(getCommand())
                .append(getName())
                .append("*\n");
        for (var block : this.Blocks) {
            builder.append(block).append("*\n");
        }
        builder.append("%");
        return builder.toString();
    }
}
