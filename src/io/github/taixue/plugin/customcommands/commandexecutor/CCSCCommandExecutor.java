package io.github.taixue.plugin.customcommands.commandexecutor;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Formatter;
import io.github.taixue.plugin.customcommands.util.Commands;
import io.github.taixue.plugin.customcommands.util.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;


public class CCSCCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // ccs.config.val
        // ccs.config.val
        Formatter.clearVariables();
        String permissionNode;
        if (strings.length == 0) {
            return false;
        }

        if (strings[0].equalsIgnoreCase("val")) {
            permissionNode = "ccs.config.val";
            Formatter.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                return val(commandSender, command, s, strings);
            }
            else {
                Formatter.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("list")) {
            permissionNode = "ccs.config.list";
            Formatter.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                return list(commandSender, command, s, strings);
            }
            else {
                Formatter.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("save") && strings.length == 1) {
            saveCommands(commandSender);
            Formatter.sendMessage(commandSender, "commandsSaved");
            return true;
        }

        if (strings[0].equalsIgnoreCase("add")) {
            permissionNode = "ccs.config.add";
            Formatter.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                boolean result = add(commandSender, command, s, strings);
                if (result && Plugin.autoSave) {
                    saveCommands(commandSender);
                }
                return result;
            }
            else {
                Formatter.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("group")) {
            permissionNode = "ccs.config.group";
            Formatter.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                boolean result = group(commandSender, command, s, strings);
                if (result && Plugin.autoSave) {
                    saveCommands(commandSender);
                }
                return result;

            }
            else {
                Formatter.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        if (strings[0].equalsIgnoreCase("remove")) {
            permissionNode = "ccs.config.remove";
            Formatter.setVariable("permission", permissionNode);
            if (commandSender.hasPermission(permissionNode)) {
                boolean result = remove(commandSender, command, s, strings);
                if (result && Plugin.autoSave) {
                    saveCommands(commandSender);
                }
                return result;

            }
            else {
                Formatter.sendMessage(commandSender, "lackPermission");
                return true;
            }
        }

        return false;
    }

    private boolean list(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            if (Plugin.commandsConfig.getGroups().isEmpty()) {
                Formatter.sendMessage(commandSender, "noAnyLoadedGroup");
            }
            else {
                Formatter.sendMessage(commandSender, "loadedGroups");
                for (Group group: Plugin.commandsConfig.getGroups()) {
                    Formatter.setVariable("group", group.getName());
                    Formatter.setVariable("size", group.getCommands().size());
                    Formatter.sendMessage(commandSender, "groupSummary");
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
                Formatter.setVariable("permission", "ccs.config.val.look");
                if (commandSender.hasPermission("ccs.config.val.look")) {
                    Object object = Plugin.pluginConfig.get("config." + valName, null);
                    if (Objects.isNull(object)) {
                        Formatter.sendMessage(commandSender, "wrongConfigItem");
                    } else {
                        Formatter.sendMessage(commandSender, valName + " : " + object);
                    }
                } else {
                    Formatter.sendMessage(commandSender, "lackPermission");
                }
                return true;
            case 3:
                Formatter.setVariable("permission", "ccs.config.val.set");
                if (commandSender.hasPermission("ccs.config.val.set")) {
                    Plugin.pluginConfig.set("config." + valName, strings[2]);
                    Plugin.saveConfig();
                    Formatter.sendMessage(commandSender, valName + " set to " + strings[2] + " completely!");
                } else {
                    Formatter.sendMessage(commandSender, "lackPermission");
                }
                return true;
            default:
                return false;
        }
    }

    private boolean add(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 2) {
            String groupName = strings[1];
            Formatter.setVariable("group", groupName);
            if (Strings.isLegalGroupName(groupName)) {
                if (Objects.isNull(Plugin.commandsConfig.getGroup(groupName))) {
                    Group group = new Group(groupName);
                    Plugin.commandsConfig.addGroup(group);
                    Formatter.sendMessage(commandSender, "groupAdded");
                } else {
                    Formatter.sendMessage(commandSender, "groupAlreadyExist");
                }
            }
            else {
                Formatter.sendMessage(commandSender, "illegalGroupName");
            }
            return true;
        }
        return false;
    }

    private boolean remove(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 2) {
            String groupName = strings[1];
            Formatter.setVariable("group", groupName);
            if (Objects.isNull(Plugin.commandsConfig.getGroup(groupName))) {
                Formatter.sendMessage(commandSender, "groupNotFound");
            }
            else {
                Plugin.commandsConfig.removeGroup(groupName);
                Formatter.sendMessage(commandSender, "groupRemoved");
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
        Formatter.setVariable("group", groupName);
        if (Objects.isNull(group)) {
            Formatter.sendMessage(commandSender, "groupNotFound");
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
        Formatter.setVariable("command", commandName);
        io.github.taixue.plugin.customcommands.customcommand.Command currentCommand = group.getCommand(commandName);

        if (firstOperator.equalsIgnoreCase("add") && strings.length == 4) {
            if (Strings.isLegalCommandName(commandName)) {
                if (Objects.isNull(currentCommand)) {

                    currentCommand = Commands.getDefaultCommand(group, commandName);
                    group.addCommand(currentCommand);
                    Formatter.sendMessage(commandSender, "commandAdded");
                } else {
                    Formatter.sendMessage(commandSender, "commandAlreadyExist");
                }
            } else {
                Formatter.sendMessage(commandSender, "illegalCommandName");
            }
            return true;
        }

        if (firstOperator.equalsIgnoreCase("remove") && strings.length == 4) {
            if (Objects.isNull(currentCommand)) {
                Formatter.sendMessage(commandSender, "commandNotFound");
            }
            else {
                group.removeCommand(commandName);
                Formatter.sendMessage(commandSender, "commandRemoved");
            }
            return true;
        }

        if (firstOperator.equalsIgnoreCase("rename") && strings.length == 4) {
            groupName = strings[3];
            Formatter.setVariable("group", groupName);
            if (Objects.isNull(Plugin.commandsConfig.getGroup(groupName))) {
                if (Strings.isLegalGroupName(groupName)) {
                    group.setName(groupName);
                    Formatter.sendMessage(commandSender, "groupRenamed");
                }
                else {
                    Formatter.sendMessage(commandSender, "illegalGroupName");
                }
            }
            else {
                Formatter.setVariable("group", "groupAlreadyExist");
            }
            return true;
        }

        if (firstOperator.equalsIgnoreCase("command")) {
            if (Objects.isNull(currentCommand)) {
                Formatter.sendMessage(commandSender, "commandNotFound");
                return true;
            }
            if (strings.length == 4) {
                showCommandDetail(commandSender, currentCommand);
                return true;
            }

            String secondOperator = strings[4];
            if (secondOperator.equalsIgnoreCase("rename") && strings.length == 6) {
                commandName = strings[5];
                Formatter.setVariable("command", commandName);
                if (Objects.isNull(group.getCommand(commandName))) {
                    if (Strings.isLegalCommandName(commandName)) {
                        currentCommand.setName(commandName);
                        Formatter.sendMessage(commandSender, "commandRenamed");
                    }
                    else {
                        Formatter.sendMessage(commandSender, "illegalCommandName");
                    }
                }
                else {
                    Formatter.sendMessage(commandSender, "commandAlreadyExist");
                }
                return true;
            }

            if (secondOperator.equalsIgnoreCase("identify") && strings.length == 6) {
                boolean set = false;
                String identify = strings[5];
                Formatter.setVariable("identify", identify);

                if (identify.equalsIgnoreCase("auto")) {
                    currentCommand.setIdentify(io.github.taixue.plugin.customcommands.customcommand.Command.Identify.AUTO);
                    set = true;
                }
                if (identify.equalsIgnoreCase("console")) {
                    currentCommand.setIdentify(io.github.taixue.plugin.customcommands.customcommand.Command.Identify.CONSOLE);
                    set = true;
                }
                if (identify.equalsIgnoreCase("bypass")) {
                    currentCommand.setIdentify(io.github.taixue.plugin.customcommands.customcommand.Command.Identify.BYPASS);
                    set = true;
                }
                if (set) {
                    Formatter.sendMessage(commandSender, "identifySet");
                }
                else {
                    Formatter.sendMessage(commandSender, "illegalIdentify");
                }
                return false;
            }

            if (secondOperator.equalsIgnoreCase("result")) {
                String result = Strings.getRemainString(strings, 5);
                Formatter.setVariable("result", result);
                currentCommand.setResultString(result);
                Formatter.sendMessage(commandSender, "resultSet");
            }

            if (secondOperator.equalsIgnoreCase("usage")) {
                String usage = Strings.getRemainString(strings, 5);
                Formatter.setVariable("usage", usage);
                currentCommand.setUsageString(usage);
                Formatter.sendMessage(commandSender, "usageSet");
            }

            if (secondOperator.equalsIgnoreCase("format")) {
                String format = Strings.getRemainString(strings, 5);
                String elderFormat = currentCommand.getFormat();

                Formatter.setVariable("format", format);
                currentCommand.setFormat(format);
                if (currentCommand.isLegalParameters()) {
                    Formatter.sendMessage(commandSender, "formatSet");
                }
                else {
                    Formatter.sendMessage(commandSender, "illegalFormat");
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

                    Formatter.setVariable("action", action);

                    currentCommand.getActions()[index] = action;
                    Formatter.sendMessage(commandSender, "actionEdited");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("clear") && strings.length == 6) {
                    currentCommand.setActions(new String[0]);
                    Formatter.sendMessage(commandSender, "actionsCleared");
                    return true;
                }

                action = Strings.getRemainString(strings, 6);
                Formatter.setVariable("action", action);

                if (lastOperator.equalsIgnoreCase("add")) {
                    currentCommand.addAction(action);
                    Formatter.sendMessage(commandSender, "actionAdded");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("remove")) {
                    if (currentCommand.containsAction(action)) {
                        currentCommand.removeAction(action);
                        Formatter.sendMessage(commandSender, "actionRemoved");
                    }
                    else {
                        Formatter.sendMessage(commandSender, "actionNotFound");
                    }
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("set")) {
                    currentCommand.setActions(new String[]{action});
                    Formatter.sendMessage(commandSender, "actionsSet");
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
                    Formatter.setVariable("permission", permission);

                    currentCommand.getPermissions()[index] = permission;
                    Formatter.sendMessage(commandSender, "permissionEdited");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("clear") && strings.length == 6) {
                    currentCommand.setPermissions(new String[0]);
                    Formatter.sendMessage(commandSender, "permissionsCleared");
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("default") && strings.length == 6) {
                    currentCommand.setPermissions(new String[]{"ccs.run." + currentCommand.getGroup() + "." + currentCommand.getName()});
                    Formatter.sendMessage(commandSender, "setPermissionsToDefault");
                    return true;
                }

                permission = strings[6];
                Formatter.setVariable("permission", permission);

                if (lastOperator.equalsIgnoreCase("add")) {
                    if (currentCommand.containsPermission(permission)) {
                        Formatter.sendMessage(commandSender, "permissionAlreadyExist");
                    }
                    else {
                        currentCommand.addPermission(permission);
                        Formatter.sendMessage(commandSender, "permissionAdded");
                    }
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("remove")) {
                    if (currentCommand.containsPermission(permission)) {
                        currentCommand.removePermissions(permission);
                        Formatter.sendMessage(commandSender, "permissionRemoved");
                    }
                    else {
                        Formatter.sendMessage(commandSender, "permissionNotFound");
                    }
                    return true;
                }

                if (lastOperator.equalsIgnoreCase("set")) {
                    currentCommand.setPermissions(new String[]{permission});
                    Formatter.sendMessage(commandSender, "permissionsSet");
                }
            }
        }

        return false;
    }

    private void showGroupDetail(CommandSender sender, Group group) {
        Formatter.setVariable("group", group.getName());
        int size = group.getCommands().size();
        Formatter.setVariable("size", size);

        Formatter.sendMessage(sender, "groupDetailsTitle");
        if (size == 0) {
            Formatter.sendMessage(sender, "noAnyLoadedCommand");
        }
        else {
            Formatter.sendMessage(sender, "loadedCommand");
            for (io.github.taixue.plugin.customcommands.customcommand.Command command : group.getCommands()) {
                Formatter.setVariable("format", command.getFormat());
                Formatter.setVariable("command", command.getName());
                Formatter.setVariable("size", command.getActions().length);
                Formatter.sendMessage(sender, "commandSummary");
            }
        }
    }

    private void showCommandDetail(CommandSender sender, io.github.taixue.plugin.customcommands.customcommand.Command command) {
        Formatter.setVariable("command", command.getName());
        Formatter.setVariable("group", command.getGroup().getName());
        Formatter.sendMessage(sender, "commandDetailsTitle");

        Formatter.sendMessageString(sender, "group: " + command.getGroup().getName());
        Formatter.sendMessageString(sender, "name: " + command.getName());
        Formatter.sendMessageString(sender, "format: " + command.getFormat());
        Formatter.sendMessageString(sender, "usage: " + command.getUsageString());
        if (command.getActions().length == 0) {
            Formatter.sendMessageString(sender, "actions (0): (empty)");
        }
        else {
            Formatter.sendMessageString(sender, "actions (" + command.getActions().length + "): ");
            for (int index = 0; index < command.getActions().length; index++) {
                Formatter.sendMessageString(sender, (index + 1) + "    > " + command.getActions()[index]);
            }
        }
        Formatter.sendMessageString(sender, "result: " + command.getResultString());
        if (command.getPermissions().length == 0) {
            Formatter.sendMessageString(sender, "permissions (0): (empty)");
        }
        else {
            Formatter.sendMessageString(sender, "permissions (" + command.getPermissions().length + "): ");
            for (int index = 0; index < command.getPermissions().length; index++) {
                Formatter.setVariable("index", index + 1);
                Formatter.sendMessageString(sender, (index + 1) + "    > " + command.getPermissions()[index]);
            }
        }
        Formatter.sendMessageString(sender, "identify: " + command.getIdentifyString());
    }

    private void saveCommands(CommandSender sender) {
        try {
            Plugin.commandsConfig.save();
        }
        catch (Exception exception) {
            Formatter.setException(exception);
            Formatter.sendMessage(sender, "exceptionInSavingCommands");
            exception.printStackTrace();
        }
    }
}
