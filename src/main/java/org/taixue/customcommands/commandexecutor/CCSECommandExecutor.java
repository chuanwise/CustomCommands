package org.taixue.customcommands.commandexecutor;

import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.language.Environment;
import org.taixue.customcommands.language.Messages;
import org.taixue.customcommands.script.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
            Messages.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                Plugin.reloadEnvironment();
                Messages.sendMessage(commandSender, "onlinePlayerEnvironmentsLoaded");
            }
            else {
                Messages.sendMessage(commandSender, "lackPermission");
            }
            return true;
        }
        String firstOperator = strings[0];
        if ((firstOperator.equalsIgnoreCase("global") ||
                firstOperator.equalsIgnoreCase("personal"))) {
            Player player;
            if (firstOperator.equalsIgnoreCase("global")) {
                permissionNode = "ccs.env.global";
                Messages.setVariable("permission", permissionNode);
                if (commandSender.hasPermission(permissionNode)) {
                    player = null;
                }
                else {
                    Messages.sendMessage(commandSender, "lackPermission");
                    return true;
                }
            }
            else {
                permissionNode = "ccs.env.personal";
                Messages.setVariable("permission", permissionNode);
                if (commandSender.hasPermission(permissionNode)) {
                    if (commandSender instanceof Player) {
                        player = ((Player) commandSender);
                    } else {
                        Messages.sendMessage(commandSender, "operatorMustBePlayer");
                        return true;
                    }
                }
                else {
                    Messages.sendMessage(commandSender, "lackPermission");
                    return true;
                }
            }

            if (strings.length == 1) {
                Map<String, String> env;
                if (Objects.isNull(player)) {
                    env = Environment.getPlayerEnvironment(null);

                    if (Objects.isNull(env) || env.isEmpty()) {
                        Messages.sendMessage(commandSender, "noAnyLoadedVariable");
                        return true;
                    }
                    HashMap<String, String> messageVariables = Messages.getEnvironment();

                    Messages.setVariable("size", messageVariables.size());
                    Messages.sendMessage(commandSender, "loadedMessageVariableTitle");
                    for (String variable: messageVariables.keySet()) {
                        Messages.sendMessageString(commandSender, "    > " + variable + ": " + messageVariables.get(variable));
                    }

                    Messages.setVariable("environment", "global");
                    Messages.setVariable("size", env.size());
                    Messages.sendMessage(commandSender, "loadedVariableTitle");

                    for (String variable: env.keySet()) {
                        Messages.sendMessageString(commandSender, "    > " + variable + ": " + env.get(variable));
                    }
                }
                else {
                    env = Environment.getPlayerEnvironment((Player) commandSender);
                    if (Objects.isNull(env) || env.isEmpty()) {
                        Messages.sendMessage(commandSender, "noAnyLoadedVariable");
                        return true;
                    }

                    Messages.setVariable("environment", "personal: " + commandSender.getName());
                    Messages.setVariable("size", env.size());
                    Messages.sendMessage(commandSender, "loadedVariableTitle");

                    for (String variable: env.keySet()) {
                        Messages.sendMessageString(commandSender, "    > " + variable + ": " + env.get(variable));
                    }
                }
                return true;
            }


            String secondOperator = strings[1];

            if (secondOperator.equalsIgnoreCase("set") && strings.length >= 3) {
                String variableName = strings[2];
                Messages.setVariable("variable", variableName);
                String value = Strings.getRemainString(strings, 3);
                Messages.setVariable("value", value);

                if (Strings.isLegalVariableName(variableName)) {
                    Environment.put(player, variableName, value);
                    Messages.sendMessage(commandSender, "variableSet");
                }
                else {
                    Messages.sendMessage(commandSender, "illegalVariableName");
                }
                return true;
            }

            // global remove <variableName>
            if (secondOperator.equalsIgnoreCase("remove") && strings.length == 3) {
                String variableName = strings[2];
                Messages.setVariable("variable", variableName);

                if (Environment.remove(player, variableName)) {
                    Messages.sendMessage(commandSender, "variableRemoved");
                }
                else {
                    Messages.sendMessage(commandSender, "variableNotFound");
                }
                return true;
            }

            // global clear
            if (secondOperator.equalsIgnoreCase("clear") && strings.length == 2) {
                Environment.clear(player);
                Messages.sendMessage(commandSender, "environmentCleared");
                return true;
            }
            return false;
        }
        return false;
    }
}
