package io.github.taixue.plugin.customcommands.commandexecutor;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.language.Formatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CCSCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            return false;
        }
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("version")) {
                Formatter.setNewVariable("permission", "ccs.version");
                if (commandSender.hasPermission("ccs.version")) {
                    Formatter.sendMessageString(commandSender, "Plugin name: " + Plugin.NAME);
                    Formatter.sendMessageString(commandSender, "Version: " + Plugin.VERSION);
                    Formatter.sendMessageString(commandSender, "Author: " + Plugin.AUTHOR);
                    Formatter.sendMessageString(commandSender, "Organization: " + Plugin.ORGANIZATION);
                    Formatter.sendMessageString(commandSender, "Github: " + Plugin.GITHUB);
                    Formatter.sendMessageString(commandSender, "Mcbbs: " + Plugin.MCBBS);
                    Formatter.sendMessageString(commandSender, "QQ group: " + Plugin.QQ_GROUP);

                    Formatter.sendMessageString(commandSender, "Remember to give me a star :)");
                    return true;
                }
                else {
                    Formatter.sendMessage(commandSender, "lackPermission");
                }
            }
            if (strings[0].equalsIgnoreCase("reload")) {
                Formatter.setNewVariable("permission", "ccs.config.reload");
                if (commandSender.hasPermission("ccs.config.reload")) {
                    Plugin.reload();
                    Formatter.infoString(commandSender.getName() + " reloaded CustomCommands.");
                    Formatter.sendMessageString(commandSender, "CustomCommands reload completely!");
                } else {
                    Formatter.sendMessage(commandSender, "lackPermission");
                }
                return true;
            }
            if (strings[0].equalsIgnoreCase("debug")) {
                Formatter.setNewVariable("permission", "ccs.config.debug");
                if (commandSender.hasPermission("ccs.config.debug")) {
                    if (Plugin.debug) {
                        Formatter.sendMessageString(commandSender, "Debug: off");
                    } else {
                        Formatter.sendMessageString(commandSender, "Debug: on");
                    }
                    Plugin.debug = !Plugin.debug;
                    Plugin.pluginConfig.set("debug", Plugin.debug);
                    Plugin.saveConfig();
                } else {
                    Formatter.sendMessageString(commandSender, "lackPermission");
                }
                return true;
            }
            else {
                return false;
            }
        }
        if (strings[0].equalsIgnoreCase("config")) {
            return Plugin.CCSC_COMMAND_EXECUTOR.onCommand(
                    commandSender,
                    command,
                    s,
                    Arrays.asList(strings).subList(1, strings.length).toArray(strings));
        }
        if (strings[0].equalsIgnoreCase("run")) {
            return Plugin.CCSR_COMMAND_EXECUTOR.onCommand(
                    commandSender,
                    command,
                    s,
                    Arrays.asList(strings).subList(1, strings.length).toArray(strings));
        }
        if (strings[0].equalsIgnoreCase("env")) {
            return Plugin.CCSE_COMMAND_EXECUTOR.onCommand(
                    commandSender,
                    command,
                    s,
                    Arrays.asList(strings).subList(1, strings.length).toArray(strings));
        }
        return false;
    }
}
