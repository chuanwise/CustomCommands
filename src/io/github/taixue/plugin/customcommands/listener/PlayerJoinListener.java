package io.github.taixue.plugin.customcommands.listener;

import io.github.taixue.plugin.customcommands.language.Environment;
import io.github.taixue.plugin.customcommands.language.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Environment.containsPlayer(player) || loadPlayerEnvironment(player)) {
            Formatter.infoLang("environmentLoaded");
        }
        else {
            Formatter.severeLanguage("failToLoadEnvironment");
        }
    }

    public boolean loadPlayerEnvironment(Player player) {
        Formatter.setVariable("environment", Objects.isNull(player) ? "global" : player.getName());
        return Environment.load(player);
    }
}