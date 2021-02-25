package io.github.taixue.plugin.customcommands.util;

import com.sun.istack.internal.NotNull;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    private Commands() {}

    @NotNull
    public static Command loadFromMemorySection(@NotNull Group addTo,
                                                @NotNull MemorySection memorySection) {
        Command result = new Command();
        result.setName(memorySection.getName());

        Messages.setVariable("command", result.getName());

        result.setFormat(((String) memorySection.get("format")));
        Messages.setVariable("format", result.getFormat());

        result.setActions(((List<String>) memorySection.get("actions")).toArray(new String[0]));

        if (memorySection.contains("usage")) {
            result.setUsageString(((String) memorySection.get("usage")));
        }
        else {
            result.setUsageString("/ccsr " + addTo.getName() + " " + result.getFormat());
        }
        Messages.setVariable("usage", result.getUsageString());

        if (memorySection.contains("identify")) {
            result.setIdentify(Command.Identify.valueOf(((String) memorySection.get("identify")).toUpperCase()));
        }
        else {
            result.setIdentify(Command.Identify.AUTO);
        }
        Messages.setVariable("identify", result.getIdentify().toString().toLowerCase());

        if (memorySection.contains("result")) {
            result.setResultString(((String) memorySection.get("result")));
        }
        else {
            result.setResultString(Messages.replaceVariableLanguage("defaultResultString"));
        }
        Messages.setVariable("result", result.getResultString());

        if (memorySection.contains("permissions")) {
            result.setPermissions(((List<String>) memorySection.get("permissions")).toArray(new String[0]));
        }
        else {
            result.setPermissions(new String[]{"ccs.run." + addTo.getName() + "." + result.getName()});
        }

        result.setMatches(new HashMap<>());
//        if (memorySection.contains("matches")) {
//            result.setMatches((Map<String, String>) (Object) ((MemorySection) memorySection.get("matches")).getValues(false));
//        }
//        else {
//            result.setMatches(new HashMap<>());
//        }

        return result;
    }

    /**
     * 检查一个 CommandSender 是否具有使用某一个指令的权限
     * @param commandSender
     * @param command
     * @return
     */
    public static boolean hasPermission(@NotNull CommandSender commandSender,
                                        @NotNull Command command) {
        boolean hasPermission = true;
        for (String permission: command.getPermissions()) {
            if (!commandSender.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static ArrayList<Command> screenUsableCommand(@NotNull CommandSender commandSender,
                                              @NotNull ArrayList<Command> commands) {
        ArrayList<Command> result = new ArrayList<>();
        for (Command command: commands) {
            if (hasPermission(commandSender, command)) {
                result.add(command);
            }
        }
        return result;
    }

    public static boolean isLegalCommandMemorySection(@NotNull MemorySection memorySection) {
        if (memorySection.contains("format") &&
                memorySection.contains("actions") &&
                memorySection.get("format") instanceof String &&
                memorySection.get("actions") instanceof List) {

            if (memorySection.contains("permissions") && !(memorySection.get("permissions") instanceof List)) {
                return false;
            }
            if (memorySection.contains("identify") && !(memorySection.get("identify") instanceof String &&
                    ((String) memorySection.get("identify")).matches("console|auto"))) {
                return false;
            }
            if (memorySection.contains("usage") && !(memorySection.get("usage") instanceof String)) {
                return false;
            }
            if (memorySection.contains("result") && !(memorySection.get("result") instanceof String)) {
                return false;
            }
            if (memorySection.contains("matches") && !(memorySection.get("matches") instanceof List)) {
                return false;
            }
            return true;
        }
        else {
            return false;
        }
    }

    @NotNull
    public static Command getDefaultCommand(@NotNull Group group, String name) {
        Command command = new Command(name);
        Messages.setVariable("group", group.getName());
        Messages.setVariable("command", command.getName());
        command.setFormat("{remain}");
        command.setActions(new String[0]);
        command.setUsageString("/ccsr " + group.getName() + " " + command.getFormat());
        command.setIdentify(Command.Identify.AUTO);
        command.setResultString(Messages.replaceVariableLanguage("defaultResultString"));
        command.setPermissions(new String[]{"ccs.run." + group.getName() + "." + command.getName()});
        return command;
    }
}
