package io.msla.gerber;

import lombok.Getter;
import lombok.Setter;

@Getter
abstract public class Layer {
    protected String name;
    protected double maxX = -99999;
    protected double minX = 99999;
    protected double maxY = -99999;
    protected double minY = 99999;
    @Setter private boolean hasGraphics = false;

    public enum Type {
        FrontCopper, FrontMask, FrontPaste, FrontSilk,
        BackCopper, BackMask, BackPaste, BackSilk,
        EdgeCuts, TopDrill, BottomDrill
    }

    public Layer(String name) {
        this.name = name;
    }

    public void clean() {
        this.maxX = -99999;
        this.minX = 99999;
        this.maxY = -99999;
        this.minY = 99999;
    }

    abstract public Type getLayerType();

    abstract public Double getWidth();
    abstract public Double getHeight();

    public static String layerTypeName(Type type) {
        return switch(type) {
            case TopDrill -> "Top Drill";
            case BottomDrill -> "Bottom Drill";
            case FrontCopper -> "Front Copper";
            case FrontMask -> "Front Mask";
            case FrontPaste -> "Front Solder Paste";
            case FrontSilk -> "Front Silkscreen";
            case BackCopper -> "Back Copper";
            case BackMask -> "Back Mask";
            case BackPaste -> "Back Solder Paste";
            case BackSilk -> "Back Silkscreen";
            case EdgeCuts -> "Outline";
        };
    }

    public static Type layerNameType(String name) {
        return switch(name) {
            case "Top Drill" -> Type.TopDrill;
            case "Bottom Drill" -> Type.BottomDrill;
            case "Front Copper" -> Type.FrontCopper;
            case "Front Mask" -> Type.FrontMask;
            case "Front Solder Paste" -> Type.FrontPaste;
            case "Front Silkscreen" -> Type.FrontSilk;
            case "Back Copper" -> Type.BackCopper;
            case "Back Mask" -> Type.BackMask;
            case "Back Solder Paste" -> Type.BackPaste;
            case "Back Silkscreen" -> Type.BackSilk;
            case "Outline" -> Type.EdgeCuts;
            default -> null;
        };
    }

    public final String layerTypeFileFunction(Type type) {
        return switch (type) {
            case FrontCopper -> "Copper,L1,Top";
            case FrontMask -> "Soldermask,Top";
            case FrontPaste -> "Paste,Top";
            case FrontSilk -> "Legend,Top";
            case BackCopper -> "Copper,L2,Bot";
            case BackMask -> "Soldermask,Bot";
            case BackPaste -> "Paste,Bot";
            case BackSilk -> "Legend,Bot";
            case EdgeCuts -> "Profile,NP";
            case TopDrill -> "Drill,Top";
            case BottomDrill -> "Drill,Bot";
        };
    }
}
