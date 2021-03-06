package org.taixue.customcommands.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.language.Environment;
import org.taixue.customcommands.language.Messages;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerQuitListener implements Listener {
    private static final int MAX_THREAD = 5;

    private ExecutorService service = Executors.newFixedThreadPool(MAX_THREAD);
    private HashMap<String, UnloadPlayerEnvironmentThread> playerThreads = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        waitPlayer(event.getPlayer());
    }

    public void waitPlayer(Player player) {
        Messages.setVariable("player", player.getName());
        if (isWaiting(player)) {
            stopWaitPlayer(player);
        }
        UnloadPlayerEnvironmentThread thread = new UnloadPlayerEnvironmentThread(player);
        playerThreads.put(player.getUniqueId().toString(), thread);
        service.execute(thread);
    }

    public void stopWaitPlayer(Player player) {
        UnloadPlayerEnvironmentThread thread = playerThreads.get(player.getUniqueId().toString());
        if (Objects.nonNull(thread)) {
            thread.stop();
            playerThreads.remove(player.getUniqueId().toString());
        }
    }

    public boolean isWaiting(Player player) {
        return playerThreads.containsKey(player.getUniqueId().toString());
    }

    public void shutdown() {
        service.shutdown();
    }
}

class UnloadPlayerEnvironmentThread extends Thread {
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UnloadPlayerEnvironmentThread(Player player) {
        setPlayer(player);
    }

    @Override
    public void run() {
        try {
            Messages.setVariable("timeout", Plugin.waitForUnload / 1000);
            Messages.infoLang("wattingPlayerLeave");
            Thread.sleep(Plugin.waitForUnload);
            Environment.unload(player);
            Messages.infoLang("unloadedPlayerEnvironment");
            Plugin.PLAYER_QUIT_LISTENER.stopWaitPlayer(player);
        }
        catch (InterruptedException e) {
        }
    }
}