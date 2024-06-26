package io.msla.gerber.gbr.cmd.d;

import lombok.Getter;
import io.msla.gerber.gbr.cmd.Command;

@Getter
public class DAperture extends Command {
    private final Integer Code;

    public DAperture(Integer code) {
        this.Code = code;
    }

    @Override
    public String toString() {
        return "D" + String.format("%d", Code) + "*" ;
    }
}
