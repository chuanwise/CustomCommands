package org.taixue.customcommands.listener;

import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.language.Environment;
import org.taixue.customcommands.language.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Messages.setVariable("player", player.getName());

        if (Plugin.PLAYER_QUIT_LISTENER.isWaiting(player)) {
            Plugin.PLAYER_QUIT_LISTENER.stopWaitPlayer(player);
            Messages.infoLang("interruptToWaitPlayerLeave");
        }
        else {
            if (!Environment.containsPlayer(player)) {
                if (loadPlayerEnvironment(player)) {
                    Messages.infoLang("environmentLoaded");
                } else {
                    Messages.severeLanguage("failToLoadEnvironment");
                }
            }
        }
    }

    public boolean loadPlayerEnvironment(Player player) {
        Messages.setVariable("environment", Objects.isNull(player) ? "global" : player.getName());
        return Environment.load(player);
    }
}