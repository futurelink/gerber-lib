package io.msla.gerber.gbr.cmd.t;

import io.msla.gerber.gbr.cmd.CommandNamed;

import java.util.Arrays;

public class TF extends CommandNamed {
    public static String GenerationSoftware = ".GenerationSoftware";
    public static String CreationDate = ".CreationDate";
    public static String ProjectId = ".ProjectId";
    public static String SameCoordinates = ".SameCoordinates";
    public static String FilePolarity = ".FilePolarity";
    public static String FileFunction = ".FileFunction";

    public TF(String name, String ... params) {
        super(name, params);
    }

    static public TF fromString(String str) {
        var t = str.substring(2).replace("*","").split(",");
        var name = t[0];
        var params = Arrays.copyOfRange(t, 1, t.length);
        return new TF(name, params);
    }
}
