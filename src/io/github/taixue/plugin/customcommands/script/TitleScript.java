package io.github.taixue.plugin.customcommands.script;

import org.bukkit.entity.Player;

public class TitleScript extends Script {
    Player player;
    String mainTitle, subTitle;

    public TitleScript(Player player) {
        setPlayer(player);
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    @Override
    public void run() {
        player.sendTitle(mainTitle, subTitle);
    }
}
