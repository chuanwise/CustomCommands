package io.github.taixue.plugin.customcommands.commandexecutor;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Messages;
import io.github.taixue.plugin.customcommands.util.Groups;
import io.github.taixue.plugin.customcommands.util.Commands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Run the command defined in commands.yml
 */
public class CCSRCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (strings.length >= 1) {
            String groupName = strings[0];
            Group group = Plugin.commandsConfig.getGroup(groupName);
            Messages.setNewVariable("group", groupName);

            if (!Groups.hasPermission(commandSender, groupName)) {
                Messages.sendMessage(commandSender, "lackPermission");
                return true;
            }
            if (Objects.isNull(group)) {
                Messages.sendMessage(commandSender, "undefinedGroup");
                return true;
            }
            try {
                ArrayList<Command> commands = Commands.screenUsableCommand(commandSender, group.getCommands(strings));

                if (commands.isEmpty()) {
                    Messages.sendMessage(commandSender, "commandNotFound");
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

                Command customCommand = commands.get(0);
                if (customCommand.parseCommand(commandSender, strings)) {
                    if (Plugin.debug) {
                        Messages.debugString(commandSender, "variable-value list:");
                        if (customCommand.getVariableValues().isEmpty()) {
                            Messages.debugString(commandSender,  "(There is no any variables)");
                        }
                        else for (String para: customCommand.getVariableValues().keySet()) {
                            Messages.debugString(commandSender,  "    > " + para + ":    " + customCommand.getVariableValues().get(para));
                        }
                    }
                }
                else {
                    Messages.sendMessage(commandSender,  "unmatchable");
                    return true;
                }

                ArrayList<String> actionStrings = customCommand.getParsedActions();

                if (Plugin.debug) {
                    Messages.debugString(commandSender, "parsed commands:");
                    if (actionStrings.isEmpty()) {
                        Messages.debugString(commandSender, "(no any parsed commands)");
                    }
                    else {
                        for (String cmd: actionStrings) {
                            Messages.debugString(commandSender,  "    > " + cmd);
                        }
                    }
                }

                for (String action : actionStrings) {
                    try {
                        switch (customCommand.getIdentify()) {
                            case AUTO:
                                if (!commandSender.getServer().dispatchCommand(commandSender, action)) {
                                    if (commandSender instanceof Player) {
                                        ((Player) commandSender).chat(action);
                                    }
                                }
                                break;
                            case CONSOLE:
                                commandSender.getServer().dispatchCommand(Plugin.plugin.getServer().getConsoleSender(), action);
                                break;
                            default:
                                Messages.sendMessage(commandSender, "wrongIdentify");
                                break;
                        }
                    }
                    catch (Exception exception) {
                        Messages.setVariable("exception", exception.toString());
                        Messages.sendMessage(commandSender, "exceptionInExecutingCommand");
                    }
                }
                Messages.sendMessageString(commandSender, customCommand.getFormattedResultString());
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
