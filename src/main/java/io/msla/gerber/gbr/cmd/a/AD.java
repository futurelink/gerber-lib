package io.msla.gerber.gbr.cmd.a;

import lombok.Getter;
import io.msla.gerber.gbr.cmd.Command;

@Getter
public class AD extends Command {
    final private int Code;
    final private String Macro;
    final private String Value;

    public AD(int code, String macro, String value) {
        this.Code = code;
        this.Macro = macro;
        this.Value = value;
    }

    static public AD fromString(String str) {
        var t = str.substring(2).replace("*","");
        var ta = t.split(",");
        var code = getDigits(ta[0], ta[0].indexOf("D") + 1);
        var codeIndex = ta[0].indexOf("D" + code);
        var n = ta[0].substring(codeIndex + ("D" + code).length());
        return new AD(code, n, ta[1]);
    }

    @Override
    public String toString() {
        return String.format("%%%sD%d%s,%s*%%", getCommand(), Code, Macro, Value);
    }
}
