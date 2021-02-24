package io.github.taixue.plugin.customcommands.customcommand;

public abstract class Script {
    protected String name;
    protected String[] arguments;

    public Script(String name) {
        setName(name);
    }

    public Script() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments;
    }

    public abstract void run();
}
