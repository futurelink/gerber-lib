package io.msla.gerber.gbr.cmd.l;

import io.msla.gerber.gbr.cmd.Command;

public class LR extends Command {
    final private Float Rotation;

    public LR(Float rot) {
        this.Rotation = rot;
    }

    @Override
    public String toString() {
        return String.format("%%%s%f*%%", getCommand(), Rotation);
    }

}
