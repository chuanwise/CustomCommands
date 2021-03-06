package io.github.taixue.plugin.customcommands.util;

import com.sun.istack.internal.NotNull;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Formatter;
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

        Formatter.setVariable("group", result.getName());

        Map<String, Object> commandMap = memorySection.getValues(false);
        Set<String> loadedCommands = new HashSet<>();
        for (String commandName: commandMap.keySet()) {
            Formatter.setVariable("command", commandName);
            try {
                MemorySection subMemorySection = ((MemorySection) commandMap.get(commandName));

                if (loadedCommands.contains(commandName)) {
                    Formatter.severeLanguage("redefinedCommands");
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
                                Formatter.severeLanguage("matchesError");
                            }
                        } else {
                            Formatter.severeLanguage("illegalParameterName");
                        }
                    } else {
                        Formatter.severeLanguage("wrongFormatForCommand");
                    }
                }
            }
            catch (Exception exception) {
                Formatter.setException(exception);
                Formatter.severeLanguage("exceptionInLoadingCommand");
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
        Formatter.setVariable("permission", permissionNode);
        return commandSender.hasPermission(permissionNode);
    }
}
