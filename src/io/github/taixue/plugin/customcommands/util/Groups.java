package io.github.taixue.plugin.customcommands.util;

import com.sun.istack.internal.NotNull;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;

import java.util.Map;

public class Groups {
    private Groups() {}

    @NotNull
    public static Group loadFromMemorySection(@NotNull MemorySection memorySection) {
        Group result = new Group();
        result.setName(memorySection.getName());

        Messages.setVariable("group", result.getName());

        Map<String, Object> commandMap = memorySection.getValues(false);
        for (String commandName: commandMap.keySet()) {
            Messages.setVariable("command", commandName);
            try {
                MemorySection subMemorySection = ((MemorySection) commandMap.get(commandName));

                if (Commands.isLegalCommandMemorySection(subMemorySection)) {
                    Command command = Commands.loadFromMemorySection(result, subMemorySection);
                    if (command.isLegalParameters()) {
                        result.addCommand(command);
                    }
                    else {
                        Messages.severeLanguage("illegalParameterName");
                    }
                }
                else {
                    Messages.severeLanguage("wrongFormatForCommand");
                }
            }
            catch (Exception exception) {
                Messages.setException(exception);
                Messages.severeLanguage("exceptionInLoadingCommand");
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
