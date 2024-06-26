package io.msla.gerber.gbr.cmd.d;

import lombok.Getter;
import io.msla.gerber.gbr.cmd.FS;

import java.math.BigDecimal;

@Getter
public class D01To03 extends DAperture {
    private final Double x;
    private final Double y;
    private final Double i;
    private final Double j;

    public D01To03(Integer code, Double x, Double y) {
        this(code, x, y, null, null);
    }

    public D01To03(Integer code, Double x, Double y, Double i, Double j) {
        super(code);
        this.x = x;
        this.y = y;
        this.i = i;
        this.j = j;
    }

    public D01To03(Integer code, String x, String y, FS format) {
        this(code, parse(x, format.getXFractional()), parse(y, format.getYFractional()));
    }

    public D01To03(Integer code, String x, String y, String i, String j, FS format) {
        this(code,
                parse(x, format.getXFractional()),
                parse(y, format.getYFractional()),
                parse(i, format.getXFractional()),
                parse(j, format.getYFractional())
        );
    }

    private static Double parse(String val, int fractionalLen) {
        if (val == null) return null;
        return BigDecimal.valueOf(Double.parseDouble(val)).movePointLeft(fractionalLen).doubleValue();
    }

    public D01To03 move(double xOffset, double yOffset) {
        return new D01To03(this.getCode(), this.x + xOffset, this.y + yOffset, i, j);
    }

    public String toString(FS format) {
        var xStr = BigDecimal.valueOf(x).movePointRight(format.getXFractional()).toBigInteger();
        var yStr = BigDecimal.valueOf(y).movePointRight(format.getYFractional()).toBigInteger();
        var iStr = ((i != null) ? "I" + BigDecimal.valueOf(i).movePointRight(format.getXFractional()).toBigInteger() : "");
        var jStr = ((j != null) ? "J" + BigDecimal.valueOf(j).movePointRight(format.getYFractional()).toBigInteger() : "");
        return String.format("X%sY%s%s%sD%02d", xStr, yStr, iStr, jStr, getCode()) + "*" ;
    }

    @Override
    public String toString() {
        var xStr = BigDecimal.valueOf(x).toPlainString();
        var yStr = BigDecimal.valueOf(y).toPlainString();
        var iStr = ((i != null) ? "I" + i : "");
        var jStr = ((j != null) ? "J" + j : "");
        return String.format("X%s Y%s %s %s D%02d", xStr, yStr, iStr, jStr, getCode());
    }
}
