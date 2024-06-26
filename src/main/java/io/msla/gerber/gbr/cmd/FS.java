package io.msla.gerber.gbr.cmd;

import lombok.Getter;

@Getter
public class FS extends Command {
    private final Boolean OmitLeadingZeroes;
    private final Boolean AbsoluteCoords;
    private Integer XInteger;
    private Integer XFractional;
    private Integer YInteger;
    private Integer YFractional;

    public FS(Boolean omitLeadingZeroes, Boolean absoluteCoords) {
        OmitLeadingZeroes = omitLeadingZeroes;
        AbsoluteCoords = absoluteCoords;
        XInteger = 4;
        XFractional = 6;
        YInteger = 4;
        YFractional = 6;
    }

    static public FS fromString(String str) {
        var t = str.substring(2).replace("*","");
        var cmd = new FS(t.contains("L"), t.contains("A"));

        var x =  t.substring(t.indexOf("X") + 1, t.indexOf("X") + 3);
        cmd.XInteger = Integer.parseInt(x.substring(0,1));
        cmd.XFractional = Integer.parseInt(x.substring(1,2));

        var y =  t.substring(t.indexOf("Y") + 1, t.indexOf("Y") + 3);
        cmd.YInteger = Integer.parseInt(y.substring(0,1));
        cmd.YFractional = Integer.parseInt(y.substring(1,2));

        return cmd;
    }

    @Override
    public String toString() {
        return "%" + getCommand() +
                (OmitLeadingZeroes ? "L" : "") +
                (AbsoluteCoords ? "A" : "") +
                "X" + XInteger + XFractional +
                "Y" + YInteger + YFractional +
                "*%";
    }
}
