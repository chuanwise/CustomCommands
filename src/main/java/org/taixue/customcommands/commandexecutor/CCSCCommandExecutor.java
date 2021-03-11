package org.taixue.customcommands.commandexecutor;

import org.bukkit.entity.Player;
import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.customcommand.Group;
import org.taixue.customcommands.language.Messages;
import org.taixue.customcommands.util.Commands;
import org.taixue.customcommands.script.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;


public class CCSCCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String permissionNode;
        if (strings.length == 0) {
            return false;
        }

        if (strings[0].equalsIgnoreCase("val") && strings.length > 1) {
            permissionNode = "ccs.config.val";
            Messages.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                return val(commandSender, command, s, strings);
            }
            else {
                Messages.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("list")) {
            permissionNode = "ccs.config.list";
            Messages.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                return list(commandSender, command, s, strings);
            }
            else {
                Messages.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("save") && strings.length == 1) {
            saveCommands(commandSender);
            Messages.sendMessage(commandSender, "commandsSaved");
            return true;
        }

        if (strings[0].equalsIgnoreCase("add")) {
            permissionNode = "ccs.config.add";
            Messages.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                boolean result = add(commandSender, command, s, strings);
                if (result && Plugin.autoSave) {
                    saveCommands(commandSender);
                }
                return result;
            }
            else {
                Messages.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("group")) {
            permissionNode = "ccs.config.group";
            Messages.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                boolean result = group(commandSender, command, s, strings);
                if (result && Plugin.autoSave) {
                    saveCommands(commandSender);
                }
                return result;

            }
            else {
                Messages.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("remove")) {
            permissionNode = "ccs.config.remove";
            Messages.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                boolean result = remove(commandSender, command, s, strings);
                if (result && Plugin.autoSave) {
                    saveCommands(commandSender);
                }
                return result;

            }
            else {
                Messages.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        return false;
    }

    private boolean list(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            if (Plugin.commandsConfig.getGroups().isEmpty()) {
                Messages.sendMessage(commandSender, "noAnyLoadedGroup");
            }
            else {
                Messages.sendMessage(commandSender, "loadedGroups");
                for (Group group: Plugin.commandsConfig.getGroups()) {
                    Messages.setVariable("group", group.getName());
                    Messages.setVariable("size", group.getCommands().size());
                    Messages.sendMessage(commandSender, "groupSummary");
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private boolean val(CommandSender commandSender, Command command, String s, String[] strings) {
        String valName = strings[1];
        switch (strings.length) {
            case 2:
                Messages.setVariable("permission", "ccs.config.val.look");
                if (commandSender.hasPermission("ccs.config.val.look")) {
                    Object object = Plugin.pluginConfig.get(valName, null);
                    if (Objects.isNull(object)) {
                        Messages.sendMessage(commandSender, "wrongConfigItem");
                    }
                    else {
                        Messages.sendMessage(commandSender, valName + " : " + object);
                    }
                } else {
                    Messages.sendMessage(commandSender, "lackPermission");
                }
                return true;
            case 3:
                Messages.setVariable("permission", "ccs.config.val.set");
                if (commandSender.hasPermission("ccs.config.val.set")) {
                    Plugin.pluginConfig.set(valName, strings[2]);
                    Plugin.saveConfigFile();
                    Messages.sendMessage(commandSender, valName + " set to " + strings[2] + " completely!");
                    Plugin.loadConfig();
                } else {
                    Messages.sendMessage(commandSender, "lackPermission");
                }
                return true;
            default:
                return false;
        }
    }

    private boolean add(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 2) {
            String groupName = strings[1];
            Messages.setVariable("group", groupName);
            if (Strings.isLegalGroupName(groupName)) {
                if (Objects.isNull(Plugin.commandsConfig.getGroup(groupName))) {
                    Group group = new Group(groupName);
                    Plugin.commandsConfig.addGroup(group);
                    Messages.sendMessage(commandSender, "groupAdded");
                } else {
                    Messages.sendMessage(commandSender, "groupAlreadyExist");
                }
            }
            else {
                Messages.sendMessage(commandSender, "illegalGroupName");
            }
            return true;
        }
        return false;
    }

    private boolean remove(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 2) {
            String groupName = strings[1];
            Messages.setVariable("group", groupName);
            if (Objects.isNull(Plugin.commandsConfig.getGroup(groupName))) {
                Messages.sendMessage(commandSender, "groupNotFound");
            }
            else {
                Plugin.commandsConfig.removeGroup(groupName);
                Messages.sendMessage(commandSender, "groupRemoved");
            }
            return true;
        }
        return false;
    }

    private boolean group(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) {
            return false;
        }
        String groupName = strings[1];
        Group group = Plugin.commandsConfig.getGroup(groupName);
        Messages.setVariable("group", groupName);
        if (Objects.isNull(group)) {
            Messages.sendMessage(commandSender, "groupNotFound");
            return true;
        }

        if (strings.length == 2) {
            showGroupDetail(commandSender, group);
            return true;
        }

        if (strings.length == 3) {
            return false;
        }

        String firstOperator = strings[2];
        String commandName = strings[3];
        Messages.setVariable("command", commandName);
        org.taixue.customcommands.customcommand.Command currentCommand = group.getCommand(commandName);

        if (firstOperator.equalsIgnoreCase("add") && strings.length == 4) {
            if (Strings.isLegalCommandName(commandName)) {
                if (Objects.isNull(currentCommand)) {

                    currentCommand = Commands.getDefaultCommand(group, commandName);
                    group.addCommand(currentCommand);
                    Messages.sendMessage(commandSender, "commandAdded");
                } else {
                    Messages.sendMessage(commandSender, "commandAlreadyExist");
                }
            } else {
                Messages.sendMessage(commandSender, "illegalCommandName");
            }
            return true;
        }

        if (firstOperator.equalsIgnoreCase("remove") && strings.length == 4) {
            if (Objects.isNull(currentCommand)) {
                Messages.sendMessage(commandSender, "commandNotFound");
            }
            else {
                group.removeCommand(commandName);
                Messages.sendMessage(commandSender, "commandRemoved");
            }
            return true;
        }

        if (firstOperator.equalsIgnoreCase("rename") && strings.length == 4) {
            groupName = strings[3];
            Messages.setVariable("group", groupName);
            if (Objects.isNull(Plugin.commandsConfig.getGroup(groupName))) {
                if (Strings.isLegalGroupName(groupName)) {
                    group.setName(groupName);
                    Messages.sendMessage(commandSender, "groupRenamed");
                }
                else {
                    Messages.sendMessage(commandSender, "illegalGroupName");
                }
            }
            else {
                Messages.setVariable("group", "groupAlreadyExist");
            }
            return true;
        }

        if (firstOperator.equalsIgnoreCase("command")) {
            if (Objects.isNull(currentCommand)) {
                Messages.sendMessage(commandSender, "commandNotFound");
                return true;
            }
            if (strings.length == 4) {
                showCommandDetail(commandSender, currentCommand);
                return true;
            }

            String secondOperator = strings[4];
            if (secondOperator.equalsIgnoreCase("rename") && strings.length == 6) {
                commandName = strings[5];
                Messages.setVariable("command", commandName);
                if (Objects.isNull(group.getCommand(commandName))) {
                    if (Strings.isLegalCommandName(commandName)) {
                        currentCommand.setName(commandName);
                        Messages.sendMessage(commandSender, "commandRenamed");
                    }
                    else {
                        Messages.sendMessage(commandSender, "illegalCommandName");
                    }
                }
                else {
                    Messages.sendMessage(commandSender, "commandAlreadyExist");
                }
                return true;
            }

            if (secondOperator.equalsIgnoreCase("identify") && strings.length == 6) {
                boolean set = false;
                String identify = strings[5];
                Messages.setVariable("identify", identify);

                if (identify.equalsIgnoreCase("auto")) {
                    currentCommand.setIdentify(org.taixue.customcommands.customcommand.Command.Identify.AUTO);
                    set = true;
                }
                if (identify.equalsIgnoreCase("console")) {
                    currentCommand.setIdentify(org.taixue.customcommands.customcommand.Command.Identify.CONSOLE);
                    set = true;
                }
                if (identify.equalsIgnoreCase("bypass")) {
                    currentCommand.setIdentify(org.taixue.customcommands.customcommand.Command.Identify.BYPASS);
                    set = true;
                }
                if (set) {
                    Messages.sendMessage(commandSender, "identifySet");
                }
                else {
                    Messages.sendMessage(commandSender, "illegalIdentify");
                }
                return false;
            }

            if (secondOperator.equalsIgnoreCase("result")) {
                String result = Strings.getRemainString(strings, 5);
                Messages.setVariable("result", result);
                currentCommand.setResultString(result);
                Messages.sendMessage(commandSender, "resultSet");
            }

            if (secondOperator.equalsIgnoreCase("usage")) {
                String usage = Strings.getRemainString(strings, 5);
                Messages.setVariable("usage", usage);
                currentCommand.setUsageString(usage);
                Messages.sendMessage(commandSender, "usageSet");
            }

            if (secondOperator.equalsIgnoreCase("format")) {
                String format = Strings.getRemainString(strings, 5);
                String elderFormat = currentCommand.getFormat();

                Messages.setVariable("format", format);
                currentCommand.setFormat(format);
                if (currentCommand.isLegalParameters()) {
                    Messages.sendMessage(commandSender, "formatSet");
                }
                else {
                    Messages.sendMessage(commandSender, "illegalFormat");
                    currentCommand.setFormat(elderFormat);
                }
                return true;
            }

            if (secondOperator.equalsIgnoreCase("actions") && strings.length >= 6) {
                String lastOperator = strings[5];
                String action;

                if (lastOperator.equalsIgnoreCase("edit") && strings.length >= 7) {
                    int index = Strings.getIndex(commandSender, strings[6], currentCommand.getActions().length);
                    if (index == -1) {
                        return true;
                    }
                    index--;
                    action = Strings.getRemainString(strings, 7);

                    Messages.setVariable("action", action);

                    currentCommand.getActions()[index] = action;
                    Messages.sendMessage(commandSender, "actionEdited");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("clear") && strings.length == 6) {
                    currentCommand.setActions(new String[0]);
                    Messages.sendMessage(commandSender, "actionsCleared");
                    return true;
                }

                action = Strings.getRemainString(strings, 6);
                Messages.setVariable("action", action);

                if (lastOperator.equalsIgnoreCase("add")) {
                    currentCommand.addAction(action);
                    Messages.sendMessage(commandSender, "actionAdded");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("remove")) {
                    if (currentCommand.containsAction(action)) {
                        currentCommand.removeAction(action);
                        Messages.sendMessage(commandSender, "actionRemoved");
                    }
                    else {
                        Messages.sendMessage(commandSender, "actionNotFound");
                    }
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("set")) {
                    currentCommand.setActions(new String[]{action});
                    Messages.sendMessage(commandSender, "actionsSet");
                    return true;
                }
            }

            if (secondOperator.equalsIgnoreCase("permissions") && strings.length >= 6) {
                String lastOperator = strings[5];
                String permission;

                if (lastOperator.equalsIgnoreCase("edit") && strings.length == 8) {
                    int index = Strings.getIndex(commandSender, strings[6], currentCommand.getPermissions().length);
                    if (index == -1) {
                        return true;
                    }
                    index--;

                    permission = strings[7];
                    Messages.setVariable("permission", permission);

                    currentCommand.getPermissions()[index] = permission;
                    Messages.sendMessage(commandSender, "permissionEdited");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("clear") && strings.length == 6) {
                    currentCommand.setPermissions(new String[0]);
                    Messages.sendMessage(commandSender, "permissionsCleared");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("default") && strings.length == 6) {
                    currentCommand.setPermissions(new String[]{"ccs.run." + currentCommand.getGroup() + "." + currentCommand.getName()});
                    Messages.sendMessage(commandSender, "setPermissionsToDefault");
                    return true;
                }

                permission = strings[6];
                Messages.setVariable("permission", permission);

                if (lastOperator.equalsIgnoreCase("add")) {
                    if (currentCommand.containsPermission(permission)) {
                        Messages.sendMessage(commandSender, "permissionAlreadyExist");
                    }
                    else {
                        currentCommand.addPermission(permission);
                        Messages.sendMessage(commandSender, "permissionAdded");
                    }
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("remove")) {
                    if (currentCommand.containsPermission(permission)) {
                        currentCommand.removePermissions(permission);
                        Messages.sendMessage(commandSender, "permissionRemoved");
                    }
                    else {
                        Messages.sendMessage(commandSender, "permissionNotFound");
                    }
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("set")) {
                    currentCommand.setPermissions(new String[]{permission});
                    Messages.sendMessage(commandSender, "permissionsSet");
                }
            }
        }

        return false;
    }

    private void showGroupDetail(CommandSender sender, Group group) {
        Messages.setVariable("group", group.getName());
        int size = group.getCommands().size();
        Messages.setVariable("size", size);

        Messages.sendMessage(sender, "groupDetailsTitle");
        if (size == 0) {
            Messages.sendMessage(sender, "noAnyLoadedCommand");
        }
        else {
            Messages.sendMessage(sender, "loadedCommand");
            for (org.taixue.customcommands.customcommand.Command command : group.getCommands()) {
                Messages.setVariable("format", command.getFormat());
                Messages.setVariable("command", command.getName());
                Messages.setVariable("size", command.getActions().length);
                Messages.sendMessage(sender, "commandSummary");
            }
        }
    }

    private void showCommandDetail(CommandSender sender, org.taixue.customcommands.customcommand.Command command) {
        Messages.setVariable("command", command.getName());
        Messages.setVariable("group", command.getGroup().getName());
        Messages.sendMessage(sender, "commandDetailsTitle");

        Messages.sendMessageString(sender, "group: " + command.getGroup().getName());
        Messages.sendMessageString(sender, "name: " + command.getName());
        Messages.sendMessageString(sender, "format: " + command.getFormat());
        Messages.sendMessageString(sender, "usage: " + command.getUsageString());
        if (command.getActions().length == 0) {
            Messages.sendMessageString(sender, "actions (0): (empty)");
        }
        else {
            Messages.sendMessageString(sender, "actions (" + command.getActions().length + "): ");
            for (int index = 0; index < command.getActions().length; index++) {
                Messages.sendMessageString(sender, (index + 1) + "    > " + command.getActions()[index]);
            }
        }
        Messages.sendMessageString(sender, "result: " + command.getResultString());
        if (command.getPermissions().length == 0) {
            Messages.sendMessageString(sender, "permissions (0): (empty)");
        }
        else {
            Messages.sendMessageString(sender, "permissions (" + command.getPermissions().length + "): ");
            for (int index = 0; index < command.getPermissions().length; index++) {
                Messages.setVariable("index", index + 1);
                Messages.sendMessageString(sender, (index + 1) + "    > " + command.getPermissions()[index]);
            }
        }
        Messages.sendMessageString(sender, "identify: " + command.getIdentifyString());
    }

    private void saveCommands(CommandSender sender) {
        try {
            Plugin.commandsConfig.save();
        }
        catch (Exception exception) {
            Messages.setException(exception);
            Messages.sendMessage(sender, "exceptionInSavingCommands");
            exception.printStackTrace();
        }
    }
}
