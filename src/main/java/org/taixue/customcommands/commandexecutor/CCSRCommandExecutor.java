package org.taixue.customcommands.commandexecutor;

import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.customcommand.Command;
import org.taixue.customcommands.customcommand.Group;
import org.taixue.customcommands.script.Script;
import org.taixue.customcommands.language.Environment;
import org.taixue.customcommands.language.Messages;
import org.taixue.customcommands.util.Commands;
import org.taixue.customcommands.script.Scripts;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Run the command defined in commands.yml
 */
public class CCSRCommandExecutor implements CommandExecutor {

    public boolean scriptRunner(String script) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (strings.length >= 1) {
            Messages.setVariable("player", commandSender.getName());
            if (commandSender instanceof Player) {
                Messages.setVariable("displayName", ((Player) commandSender).getDisplayName());
                Messages.setVariable("UUID", ((Player) commandSender).getUniqueId().toString());
                Messages.setVariable("world", ((Player) commandSender).getWorld().getName());
            }
            String groupName = strings[0];
            Group group = Plugin.commandsConfig.getGroup(groupName);
            Messages.setVariable("group", groupName);

            if (Objects.isNull(group)) {
                Messages.sendMessage(commandSender, "undefinedGroup");
                return true;
            }
            try {
                ArrayList<Command> matchableCommands = group.getCommands(strings);
                ArrayList<Command> commands = Commands.screenUsableCommand(commandSender, matchableCommands);

                if (commands.isEmpty()) {
                    if (matchableCommands.isEmpty()) {
                        Messages.sendMessage(commandSender, "noMatchableCommand");
                        ArrayList<Command> allCommands = Commands.screenUsableCommand(commandSender, group.getCommands());

                        if (!allCommands.isEmpty()) {
                            Messages.sendMessage(commandSender, "loadedCommand");
                            for (Command cmd : allCommands) {
                                Messages.sendMessage(commandSender, cmd.getUsageString());
                            }
                        }
                    }
                    else {
                        Messages.sendMessage(commandSender, "noPermissionToMatch");
                    }
                    return true;
                }
                else if (commands.size() != 1) {
                    Messages.setVariable("size", Integer.toString(commands.size()));
                    Messages.sendMessage(commandSender, "multipleCommands");
                    for (Command cmd : commands) {
                        Messages.sendMessageString(commandSender, cmd.getUsageString());
                    }
                    return true;
                }

                // personal variables
                if (Plugin.debug) {
                    Map<String, String> personalEnvironment;
                    if (commandSender instanceof Player) {
                        personalEnvironment = Environment.getPlayerEnvironment((Player) commandSender);
                        Messages.debugString(commandSender, "personal variables:");
                        if (personalEnvironment.isEmpty()) {
                            Messages.debugString(commandSender, "(no any personal variables)");
                        }
                        else {
                            for (String variableName: personalEnvironment.keySet()) {
                                Messages.debugString(commandSender,  "    > " + variableName + ": " + personalEnvironment.get(variableName));
                            }
                        }
                    }
                    else {
                        Messages.debugString(commandSender, "No personal variables, because command sender isn't a player.");
                    }
                }

                // global variables
                if (Plugin.debug) {
                    Map<String, String> globalEnvironment = Environment.getPlayerEnvironment(null);
                    Messages.debugString(commandSender, "global variables:");
                    if (Objects.isNull(globalEnvironment) || globalEnvironment.isEmpty()) {
                        Messages.debugString(commandSender, "(no any global variables)");
                    }
                    else {
                        for (String variableName: globalEnvironment.keySet()) {
                            Messages.debugString(commandSender,  "    > " + variableName + ": " + globalEnvironment.get(variableName));
                        }
                    }
                }

                Command customCommand = commands.get(0);
                if (customCommand.parseCommand(commandSender, strings)) {
                    if (Plugin.debug) {
                        Messages.debugString(commandSender, "variable-value list:");
                        if (customCommand.getVariableValues().isEmpty()) {
                            Messages.debugString(commandSender,  "(There is no any variables)");
                        }
                        else for (String para: customCommand.getVariableValues().keySet()) {
                            Messages.debugString(commandSender,  "    > " + para + ": " + customCommand.getVariableValues().get(para));
                        }
                    }
                }
                else {
                    Messages.sendMessage(commandSender,  "unmatchable");
                    return true;
                }

                CommandSender actionSender;
                Player player = null;
                boolean isOp = commandSender.isOp();

                switch (customCommand.getIdentify()) {
                    case AUTO:
                        actionSender = commandSender;
                        break;
                    case BYPASS:
                        commandSender.setOp(true);
                        actionSender = commandSender;
                        break;
                    case CONSOLE:
                        actionSender = Bukkit.getConsoleSender();
                        break;
                    default:
                        actionSender = commandSender;
                        Messages.sendMessage(commandSender, "illegalIdentify");
                        break;
                }
                if (commandSender instanceof Player) {
                    player = (Player) commandSender;
                }

                if (Plugin.debug) {
                    Messages.debugString(commandSender, "executing...");
                    if (customCommand.getActions().length == 0) {
                        Messages.debugString(commandSender, "(no any action command)");
                    }
                }

                for (String unparsedCommand: customCommand.getActions()) {
                    if (Plugin.debug) {
                        Messages.debugString(commandSender, "unparsed: " + unparsedCommand);
                    }
                    String action = Messages.replaceVariableString(player, customCommand.replaceVariables(unparsedCommand));
                    if (Plugin.debug) {
                        Messages.debugString(commandSender, "parse to: " + action);
                    }
                    if (action.startsWith("@")) {
                        Script script = Scripts.parseScript(action, actionSender);
                        if (Objects.nonNull(script)) {
                            script.run();
                        }
                        continue;
                    }
                    try {
                        if (!commandSender.getServer().dispatchCommand(actionSender, action)) {
                            if (commandSender instanceof Player) {
                                ((Player) commandSender).chat(action);
                            }
                        }
                    }
                    catch (Exception exception) {
                        Messages.setVariable("exception", exception.toString());
                        Messages.sendMessage(commandSender, "exceptionInExecutingCommand");
                        exception.printStackTrace();
                    }
                }

                if (customCommand.getIdentify() == Command.Identify.BYPASS) {
                    commandSender.setOp(isOp);
                }

                if (Objects.nonNull(customCommand.getResultString())) {
                    Messages.sendMessageString(commandSender, customCommand.getFormattedResultString());
                }
            }
            catch (Exception exception) {
                Messages.setVariable("exception", exception.toString());
                Messages.sendMessage(commandSender, "unknownException");
                exception.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }
}
