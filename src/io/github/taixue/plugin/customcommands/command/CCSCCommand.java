package io.github.taixue.plugin.customcommands.command;

import io.github.taixue.plugin.customcommands.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

/**
 * Custom Commands Config
 */
public class CCSCCommand implements CommandExecutor {
    /**
     * @param commandSender
     * @param command
     * @param s
     * @param strings   arguments of command.
     * @return
     */

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length >= 2) {
            if (strings[0].equalsIgnoreCase("val")) {
                String valName = strings[1];
                if (commandSender.hasPermission("ccs.config.val.look")) {
                    if (strings.length == 2) {
                        Object object = Plugin.configuration.get("config." + valName, null);
                        if (Objects.isNull(object)) {
                            commandSender.sendMessage(Plugin.language.noAnyLoadedCommand.replaceAll("\\{variable}", valName));
                        } else {
                            commandSender.sendMessage(valName + " = " + object);
                        }
                        return true;
                    }
                }
                else {
                    commandSender.sendMessage(Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.config.val.look"));
                    return true;
                }
                if (commandSender.hasPermission("ccs.config.val.set")) {
                    if (strings.length == 3) {
                        Plugin.configuration.set("config." + valName, strings[2]);
                        Plugin.saveConfig();
                        commandSender.sendMessage(valName + " set to " + strings[2] + " completely!");
                        return true;
                    }
                }
                else {
                    commandSender.sendMessage(Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.config.val.set"));
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
