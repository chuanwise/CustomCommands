package io.github.taixue.plugin.customcommands.config;

import io.github.taixue.plugin.customcommands.Plugin;

import java.io.File;

public class PluginConfig extends Config{
    public PluginConfig(File file, String head) {
        super(file, head);
    }

    public PluginConfig() {
        this(new File(Plugin.plugin.getDataFolder(), "config.yml"), "config");
    }
}
