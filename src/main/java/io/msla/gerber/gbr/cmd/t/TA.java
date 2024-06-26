package io.msla.gerber.gbr.cmd.t;

import io.msla.gerber.gbr.cmd.CommandNamed;

import java.util.Arrays;

public class TA extends CommandNamed {

    public TA(String name, String... params) {
        super(name, params);
    }

    static public TA fromString(String str) {
        var t = str.substring(2).replace("*","").split(",");
        var name = t[0];
        var params = Arrays.copyOfRange(t, 1, t.length);
        return new TA(name, params);
    }
}
