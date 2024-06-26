package io.msla.gerber.gbr.cmd.l;

import io.msla.gerber.gbr.cmd.Command;

public class LS extends Command {
    final private Float Scale;

    public LS(Float sc) {
        this.Scale = sc;
    }

    @Override
    public String toString() {
        return String.format("%%%s%f*%%", getCommand(), Scale);
    }
}
