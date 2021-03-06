package io.github.taixue.plugin.customcommands.commandexecutor;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.language.Environment;
import io.github.taixue.plugin.customcommands.language.Formatter;
import io.github.taixue.plugin.customcommands.util.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;


public class CCSECommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            return false;
        }
        String permissionNode = null;
        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            permissionNode = "ccs.env.reload";
            Formatter.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                Plugin.reloadEnvironment();
                Formatter.sendMessage(commandSender, "onlinePlayerEnvironmentsLoaded");
            }
            else {
                Formatter.sendMessage(commandSender, "lackPermission");
            }
            return true;
        }
        String firstOperator = strings[0];
        if ((firstOperator.equalsIgnoreCase("global") ||
                firstOperator.equalsIgnoreCase("personal"))) {
            Player player;
            if (firstOperator.equalsIgnoreCase("global")) {
                permissionNode = "ccs.env.global";
                Formatter.setVariable("permission", permissionNode);
                if (commandSender.hasPermission(permissionNode)) {
                    player = null;
                }
                else {
                    Formatter.sendMessage(commandSender, "lackPermission");
                    return true;
                }
            }
            else {
                permissionNode = "ccs.env.personal";
                Formatter.setVariable("permission", permissionNode);
                if (commandSender.hasPermission(permissionNode)) {
                    if (commandSender instanceof Player) {
                        player = ((Player) commandSender);
                    } else {
                        Formatter.sendMessage(commandSender, "operatorMustBePlayer");
                        return true;
                    }
                }
                else {
                    Formatter.sendMessage(commandSender, "lackPermission");
                    return true;
                }
            }

            if (strings.length == 1) {
                Map<String, String> env;
                if (Objects.isNull(player)) {
                    env = Environment.getPlayerEnvironment(null);

                    if (Objects.isNull(env) || env.isEmpty()) {
                        Formatter.sendMessage(commandSender, "noAnyLoadedVariable");
                        return true;
                    }

                    Formatter.setVariable("environment", "global");
                    Formatter.setVariable("size", env.size());
                    Formatter.sendMessage(commandSender, "loadedVariableTitle");

                    for (String variable: env.keySet()) {
                        Formatter.sendMessageString(commandSender, "    > " + variable + ": " + env.get(variable));
                    }
                }
                else {
                    env = Environment.getPlayerEnvironment((Player) commandSender);
                    if (Objects.isNull(env) || env.isEmpty()) {
                        Formatter.sendMessage(commandSender, "noAnyLoadedVariable");
                        return true;
                    }

                    Formatter.setVariable("environment", "personal: " + commandSender.getName());
                    Formatter.setVariable("size", env.size());
                    Formatter.sendMessage(commandSender, "loadedVariableTitle");

                    for (String variable: env.keySet()) {
                        Formatter.sendMessageString(commandSender, "    > " + variable + ": " + env.get(variable));
                    }
                }
                return true;
            }


            String secondOperator = strings[1];

            if (secondOperator.equalsIgnoreCase("set") && strings.length >= 3) {
                String variableName = strings[2];
                Formatter.setVariable("variable", variableName);
                String value = Strings.getRemainString(strings, 3);
                Formatter.setVariable("value", value);

                if (Strings.isLegalVariableName(variableName)) {
                    Environment.put(player, variableName, value);
                    Formatter.sendMessage(commandSender, "variableSet");
                }
                else {
                    Formatter.sendMessage(commandSender, "illegalVariableName");
                }
                return true;
            }

            // global remove <variableName>
            if (secondOperator.equalsIgnoreCase("remove") && strings.length == 3) {
                String variableName = strings[2];
                Formatter.setVariable("variable", variableName);

                if (Environment.remove(player, variableName)) {
                    Formatter.sendMessage(commandSender, "variableRemoved");
                }
                else {
                    Formatter.sendMessage(commandSender, "variableNotFound");
                }
                return true;
            }

            // global clear
            if (secondOperator.equalsIgnoreCase("clear") && strings.length == 2) {
                Environment.clear(player);
                Formatter.sendMessage(commandSender, "environmentCleared");
                return true;
            }
            return false;
        }
        return false;
    }
}
