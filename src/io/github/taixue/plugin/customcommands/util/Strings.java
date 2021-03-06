package io.github.taixue.plugin.customcommands.util;

import com.sun.istack.internal.NotNull;
import io.github.taixue.plugin.customcommands.language.Formatter;
import org.bukkit.command.CommandSender;

public class Strings {
    private Strings() {}

    public static final String LEGAL_COMMAND_NAME_REGEX = "[a-zA-z0-9_][a-zA-z0-9_\\-]*";
    public static final String LEGAL_VARIABLE_NAME_REGEX = "[a-zA-z_][a-zA-z0-9_]*";
    public static final String LEGAL_GROUP_NAME_REGEX = "[^\\-].*";

    public static final String DEFAULT_REGEX = ".+";

    public static boolean isLegalCommandName(@NotNull String commandName) {
        return commandName.matches(LEGAL_COMMAND_NAME_REGEX);
    }

    public static boolean isLegalVariableName(@NotNull String variableName) {
        return variableName.matches(LEGAL_VARIABLE_NAME_REGEX);
    }

    public static boolean isLegalGroupName(@NotNull String variableName) {
        return variableName.matches(LEGAL_GROUP_NAME_REGEX);
    }


    public static String getRemainString(String[] strings, int beginIndex) {
        if (strings.length <= beginIndex) {
            return "";
        }
        StringBuilder resultBuilder = new StringBuilder(strings[beginIndex]);
        for (int index = beginIndex + 1; index < strings.length; index++) {
            resultBuilder.append(" ").append(strings[index]);
        }
        return resultBuilder.toString();
    }

    public static int getIndex(CommandSender commandSender, String indexString, int top) {
        Formatter.setVariable("index", indexString);
        Formatter.setVariable("top", top);
        if (indexString.matches("\\d+")) {
            int result = Integer.parseInt(indexString);
            if (result <= top && result >= 1) {
                return result;
            }
            else {
                Formatter.sendMessage(commandSender, "illegalIndex");
                return -1;
            }
        }
        else {
            Formatter.sendMessage(commandSender, "illegalIndex");
            return -1;
        }
    }
}
