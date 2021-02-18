package io.github.taixue.plugin.customcommands.command;

import io.github.taixue.plugin.customcommands.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class CCSCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // ccs reload
        if (strings.length == 0) {
            return false;
        }
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("ccs.reload")) {
                    Plugin.reload();
                    commandSender.sendMessage("CustomCommands reload completely!");
                } else {
                    commandSender.sendMessage(Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.reload"));
                }
                return true;
            }
            if (strings[0].equalsIgnoreCase("debug")) {
                if (commandSender.hasPermission("ccs.debug")) {
                    if (Plugin.debug) {
                        commandSender.sendMessage("Debug: off");
                    } else {
                        commandSender.sendMessage("Debug: on");
                    }
                    Plugin.debug = !Plugin.debug;
                    Plugin.saveConfig();
                } else {
                    commandSender.sendMessage(Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.debug"));
                }
                return true;
            } else {
                return false;
            }
        }
        if (strings[0].equalsIgnoreCase("config")) {
            return ((CCSRCommand) Plugin.plugin.getCommand("customcommandsconfig").getExecutor()).
                    onCommand(commandSender, command, s, Arrays.asList(strings).subList(1, strings.length).toArray(strings));
        }
        if (strings[0].equalsIgnoreCase("run")) {
            return ((CCSRCommand) Plugin.plugin.getCommand("customcommandsrun").getExecutor()).
                    onCommand(commandSender, command, s, Arrays.asList(strings).subList(1, strings.length).toArray(strings));
        }
        return false;
    }
}
