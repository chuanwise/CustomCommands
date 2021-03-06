package io.github.taixue.plugin.customcommands.script;

public abstract class Script {
    protected String name;

    public Script(String name) {
        setName(name);
    }

    public Script() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void run();
}