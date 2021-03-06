package org.taixue.customcommands.config;

import org.taixue.customcommands.Plugin;

import java.io.File;
import java.io.IOException;

public class PluginConfig extends Config{
    public PluginConfig(File file, String head) {
        super(file, head);
    }

    public PluginConfig() {
        this(new File(Plugin.plugin.getDataFolder(), "config.yml"), "config");
    }

    @Override
    public void save() throws IOException {
        super.save();
    }
}
