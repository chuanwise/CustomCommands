package io.github.taixue.plugin.customcommands.command;

import io.github.taixue.plugin.customcommands.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CCSCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // ccs reload
        if (strings.length == 0) {
            return false;
        }
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("version")) {
                if (commandSender.hasPermission("ccs.version")) {
                    commandSender.sendMessage(Plugin.language.messageHead + "Plugin name: " + Plugin.NAME);
                    commandSender.sendMessage(Plugin.language.messageHead + "Version: " + Plugin.VERSION);
                    commandSender.sendMessage(Plugin.language.messageHead + "Github: " + Plugin.GITHUB);
                    commandSender.sendMessage(Plugin.language.messageHead + "QQ group: " + Plugin.QQ_GROUP);
                    return true;
                }
                else {
                    commandSender.sendMessage(Plugin.language.messageHead +
                            Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.version"));
                }
            }
            if (strings[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("ccs.reload")) {
                    Plugin.reload();
                    Plugin.logger.info(commandSender.getName() + " reloaded CustomCommands.");
                    commandSender.sendMessage(Plugin.language.messageHead + "CustomCommands reload completely!");
                } else {
                    Plugin.logger.info(commandSender.getName() + " want to reload CustomCommands, " +
                            "but don't have enough permissions.");
                    commandSender.sendMessage(Plugin.language.messageHead +
                            Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.reload"));
                }
                return true;
            }
            if (strings[0].equalsIgnoreCase("debug")) {
                if (commandSender.hasPermission("ccs.debug")) {
                    if (Plugin.debug) {
                        commandSender.sendMessage(Plugin.language.messageHead + "Debug: off");
                    } else {
                        commandSender.sendMessage(Plugin.language.messageHead + "Debug: on");
                    }
                    Plugin.debug = !Plugin.debug;
                    Plugin.logger.info(commandSender.getName() + " reversed debug mode to " + Plugin.debug);
                    Plugin.saveConfig();
                } else {
                    Plugin.logger.info(commandSender.getName() + " want to reverse debug mode, " +
                            "but don't have enough permissions.");
                    commandSender.sendMessage(Plugin.language.messageHead +
                            Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.debug"));
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
