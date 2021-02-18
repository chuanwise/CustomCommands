package io.github.taixue.plugin.customcommands;

import org.bukkit.plugin.java.JavaPlugin;

public class CustomCommandPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Plugin.load(this);
    }

    @Override
    public void onDisable() {
        Plugin.close();
    }
}
