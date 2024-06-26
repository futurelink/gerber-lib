package io.msla.gerber.gbr.cmd.g;

import lombok.Getter;
import io.msla.gerber.gbr.cmd.Command;

@Getter
public class GCode extends Command {
    private final int Code;
    private final String Param;

    public GCode(int code, String param) {
        this.Code = code;
        this.Param = param;
    }

    @Override
    public String toString() {
        return "G" + String.format("%02d%s", Code, (Param != null) ? Param : "") + "*";
    }
}
