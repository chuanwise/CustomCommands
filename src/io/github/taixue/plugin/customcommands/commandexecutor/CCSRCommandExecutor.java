package io.github.taixue.plugin.customcommands.commandexecutor;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.script.Script;
import io.github.taixue.plugin.customcommands.language.Environment;
import io.github.taixue.plugin.customcommands.language.Formatter;
import io.github.taixue.plugin.customcommands.util.Commands;
import io.github.taixue.plugin.customcommands.util.Scripts;
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
        Formatter.clearVariables();
        if (strings.length >= 1) {
            Formatter.setVariable("player", commandSender.getName());
            if (commandSender instanceof Player) {
                Formatter.setVariable("displayName", ((Player) commandSender).getDisplayName());
                Formatter.setVariable("UUID", ((Player) commandSender).getUniqueId().toString());
                Formatter.setVariable("world", ((Player) commandSender).getWorld().getName());
            }
            String groupName = strings[0];
            Group group = Plugin.commandsConfig.getGroup(groupName);
            Formatter.setVariable("group", groupName);

            if (Objects.isNull(group)) {
                Formatter.sendMessage(commandSender, "undefinedGroup");
                return true;
            }
            try {
                ArrayList<Command> matchableCommands = group.getCommands(strings);
                ArrayList<Command> commands = Commands.screenUsableCommand(commandSender, matchableCommands);

                if (commands.isEmpty()) {
                    if (matchableCommands.isEmpty()) {
                        Formatter.sendMessage(commandSender, "noMatchableCommand");
                        ArrayList<Command> allCommands = Commands.screenUsableCommand(commandSender, group.getCommands());

                        if (!allCommands.isEmpty()) {
                            Formatter.sendMessage(commandSender, "loadedCommand");
                            for (Command cmd : allCommands) {
                                Formatter.sendMessage(commandSender, cmd.getUsageString());
                            }
                        }
                    }
                    else {
                        Formatter.sendMessage(commandSender, "noPermissionToMatch");
                    }
                    return true;
                }
                else if (commands.size() != 1) {
                    Formatter.setVariable("size", Integer.toString(commands.size()));
                    Formatter.sendMessage(commandSender, "multipleCommands");
                    for (Command cmd : commands) {
                        Formatter.sendMessageString(commandSender, cmd.getUsageString());
                    }

                    return true;
                }

                // personal variables
                if (Plugin.debug) {
                    Map<String, String> personalEnvironment;
                    if (commandSender instanceof Player) {
                        personalEnvironment = Environment.getPlayerEnvironment((Player) commandSender);
                        Formatter.debugString(commandSender, "personal variables:");
                        if (personalEnvironment.isEmpty()) {
                            Formatter.debugString(commandSender, "(no any personal variables)");
                        }
                        else {
                            for (String variableName: personalEnvironment.keySet()) {
                                Formatter.debugString(commandSender,  "    > " + variableName + ": " + personalEnvironment.get(variableName));
                            }
                        }
                    }
                    else {
                        Formatter.debugString(commandSender, "No personal variables, because command sender isn't a player.");
                    }
                }

                // global variables
                if (Plugin.debug) {
                    Map<String, String> globalEnvironment = Environment.getPlayerEnvironment(null);
                    Formatter.debugString(commandSender, "global variables:");
                    if (Objects.isNull(globalEnvironment) || globalEnvironment.isEmpty()) {
                        Formatter.debugString(commandSender, "(no any global variables)");
                    }
                    else {
                        for (String variableName: globalEnvironment.keySet()) {
                            Formatter.debugString(commandSender,  "    > " + variableName + ": " + globalEnvironment.get(variableName));
                        }
                    }
                }

                Command customCommand = commands.get(0);
                if (customCommand.parseCommand(commandSender, strings)) {
                    if (Plugin.debug) {
                        Formatter.debugString(commandSender, "variable-value list:");
                        if (customCommand.getVariableValues().isEmpty()) {
                            Formatter.debugString(commandSender,  "(There is no any variables)");
                        }
                        else for (String para: customCommand.getVariableValues().keySet()) {
                            Formatter.debugString(commandSender,  "    > " + para + ": " + customCommand.getVariableValues().get(para));
                        }
                    }
                }
                else {
                    Formatter.sendMessage(commandSender,  "unmatchable");
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
                        Formatter.sendMessage(commandSender, "illegalIdentify");
                        break;
                }
                if (commandSender instanceof Player) {
                    player = (Player) commandSender;
                }

                if (Plugin.debug) {
                    Formatter.debugString(commandSender, "executing...");
                    if (customCommand.getActions().length == 0) {
                        Formatter.debugString(commandSender, "(no any action command)");
                    }
                }

                for (String unparsedCommand: customCommand.getActions()) {
                    if (Plugin.debug) {
                        Formatter.debugString(commandSender, "unparsed: " + unparsedCommand);
                    }
                    String action = Formatter.replaceVariableString(player, customCommand.replaceVariables(unparsedCommand));
                    if (Plugin.debug) {
                        Formatter.debugString(commandSender, "parse to: " + action);
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
                        Formatter.setVariable("exception", exception.toString());
                        Formatter.sendMessage(commandSender, "exceptionInExecutingCommand");
                        exception.printStackTrace();
                    }
                }

                if (customCommand.getIdentify() == Command.Identify.BYPASS) {
                    commandSender.setOp(isOp);
                }

                Formatter.sendMessageString(commandSender, customCommand.getFormattedResultString());
            }
            catch (Exception exception) {
                Formatter.setVariable("exception", exception.toString());
                Formatter.sendMessage(commandSender, "unknownException");
                exception.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }
}
