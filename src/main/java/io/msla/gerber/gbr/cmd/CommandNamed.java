package io.msla.gerber.gbr.cmd;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public abstract class CommandNamed extends Command {
    private final String Name;
    private final ArrayList<String> Params;

    public CommandNamed(String name) {
        this.Name = name;
        this.Params = new ArrayList<>();
    }

    public CommandNamed(String name, String ... params) {
        this(name);
        Params.addAll(Arrays.asList(params));
    }

    @Override
    public String toString() {
        return "%" +
                getCommand() +
                getName() +
                (Params.isEmpty() ? "" : "," + String.join(",", getParams())) +
                "*%";
    }

}
