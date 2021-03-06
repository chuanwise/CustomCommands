package org.taixue.customcommands.util;

import com.sun.istack.internal.NotNull;
import org.taixue.customcommands.customcommand.Command;
import org.taixue.customcommands.customcommand.Group;
import org.taixue.customcommands.language.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Groups {
    private Groups() {}

    @NotNull
    public static Group loadFromMemorySection(@NotNull MemorySection memorySection) {
        Group result = new Group();
        result.setName(memorySection.getName());

        Messages.setVariable("group", result.getName());

        Map<String, Object> commandMap = memorySection.getValues(false);
        Set<String> loadedCommands = new HashSet<>();
        for (String commandName: commandMap.keySet()) {
            Messages.setVariable("command", commandName);
            try {
                MemorySection subMemorySection = ((MemorySection) commandMap.get(commandName));

                if (loadedCommands.contains(commandName)) {
                    Messages.severeLanguage("redefinedCommands");
                }
                else {
                    if (Commands.isLegalCommandMemorySection(subMemorySection)) {
                        loadedCommands.add(commandName);
                        Command command = Commands.loadFromMemorySection(result, subMemorySection);
                        if (command.isLegalParameters()) {
                            if (command.isLegalMatches()) {
                                result.addCommand(command);
                            }
                            else {
                                Messages.severeLanguage("matchesError");
                            }
                        } else {
                            Messages.severeLanguage("illegalParameterName");
                        }
                    } else {
                        Messages.severeLanguage("wrongFormatForCommand");
                    }
                }
            }
            catch (Exception exception) {
                Messages.setException(exception);
                Messages.severeLanguage("exceptionInLoadingCommand");
                exception.printStackTrace();
            }
        }
        return result;
    }

    public static boolean hasPermission(@NotNull CommandSender commandSender, @NotNull Group group) {
        return hasPermission(commandSender, group.getName());
    }

    public static boolean hasPermission(@NotNull CommandSender commandSender, @NotNull String groupName) {
        String permissionNode = "ccs.run." + groupName;
        Messages.setVariable("permission", permissionNode);
        return commandSender.hasPermission(permissionNode);
    }
}
