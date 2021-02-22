package io.github.taixue.plugin.customcommands.commandexecutor;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.language.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;


public class CCSCCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // ccs.config.val.look
        // ccs.config.val.set
        String permissionNode;
        if (strings[0].equalsIgnoreCase("val")) {
            String valName = strings[1];
            switch (strings.length) {
                case 2:
                    Messages.setVariable("permission", "ccs.config.val.look");
                    if (commandSender.hasPermission("ccs.config.val.look")) {
                        Object object = Plugin.pluginConfig.get("config." + valName, null);
                        if (Objects.isNull(object)) {
                            Messages.sendMessage(commandSender, "wrongConfigItem");
                        } else {
                            Messages.sendMessage(commandSender, valName + " : " + object);
                        }
                    } else {
                        Messages.sendMessage(commandSender, "lackPermission");
                    }
                    return true;
                case 3:
                    Messages.setVariable("permission", "ccs.config.val.set");
                    if (commandSender.hasPermission("ccs.config.val.set")) {
                        Plugin.pluginConfig.set("config." + valName, strings[2]);
                        Plugin.saveConfig();
                        Messages.sendMessage(commandSender, valName + " set to " + strings[2] + " completely!");
                    } else {
                        Messages.sendMessage(commandSender, "lackPermission");
                    }
                    return true;
                default:
                    return false;
            }
        }
//
//        if (strings[0].equalsIgnoreCase("add")) {
//            if (strings.length == 2) {
//                permissionNode = "ccs.config.add";
//                Messages.setVariable("permission", permissionNode);
//                if (commandSender.hasPermission(permissionNode)) {
//                    String newGroupName = strings[1];
//                    Messages.setVariable("group", newGroupName);
//                    if (Objects.nonNull(Plugin.commandsConfig.getGroup(newGroupName))) {
//                        Group Group = new Group();
//                        Group.setName();
//                        Plugin.commandsConfig.addGroup();
//                    }
//                    else {
//                        Messages.sendMessage(commandSender, "redefinedGroup");
//                    }
//                }
//                else {
//                    Messages.sendMessage(commandSender, "lackPermission");
//                }
//            }
//            else {
//                Messages.sendMessageString(commandSender, "Usage: /ccsc add <new-group-name>");
//            }
//            return true;
//        }
//
//        if (strings[0].equalsIgnoreCase("group")) {
//            Group Group;
//            io.github.taixue.plugin.customcommands.customcommand.Command currentCommand;
//            switch (strings.length) {
//                case 1:
//                    permissionNode = "ccs.config.group";
//                    Messages.setVariable("permission", permissionNode);
//                    if (commandSender.hasPermission(permissionNode)) {
//                        if (Plugin.commandsConfig.getGroups().isEmpty()) {
//                            Messages.sendMessage(commandSender, "noAnyLoadedGroup");
//                        } else {
//                            for (Group group : Plugin.commandsConfig.getGroups()) {
//                                Messages.sendMessage(commandSender, group.getName());
//                            }
//                        }
//                    }
//                    else {
//                        Messages.sendMessage(commandSender, "lackPermission");
//                    }
//                    break;
//                case 2:
//                    permissionNode = "ccs.config.group." + strings[1];
//                    if (commandSender.hasPermission(permissionNode)) {
//                        Messages.setVariable("group", strings[1]);
//                        Group = Plugin.commandsConfig.getGroup(strings[1]);
//                        if (Objects.isNull(Group)) {
//                            Messages.sendMessage(commandSender, "groupNotFound");
//                        } else {
//                            if (Group.getCommands().isEmpty()) {
//                                Messages.sendMessage(commandSender, "noAnyLoadedCommand");
//                            } else {
//                                Messages.sendMessage(commandSender, "loadedCommand");
//                                for (io.github.taixue.plugin.customcommands.customcommand.Command cmd : Group.getCommands()) {
//                                    Messages.sendMessage(commandSender, cmd.getName());
//                                }
//                            }
//                        }
//                    }
//                    else {
//                        Messages.sendMessage(commandSender, "lackPermission");
//                    }
//                    break;
//                case 3:
//                    permissionNode = "ccs.config.group." + strings[1];
//                    if (commandSender.hasPermission(permissionNode)) {
//                        switch (strings[2]) {
//
//                        }
//                    }
//                    else {
//                        Messages.sendMessage(commandSender, "lackPermission");
//                    }
//                    break;
//                default:
//                    return false;
//            }
//            return true;
//        }
        return false;
    }
}
