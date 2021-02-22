package io.github.taixue.plugin.customcommands.util;

import com.sun.istack.internal.NotNull;

public class Strings {
    private Strings() {}

    public static final String LEGAL_COMMAND_NAME_REGEX = "[a-zA-z0-9_][a-zA-z0-9_\\-]*";
    public static final String LEGAL_VARIABLE_NAME_REGEX = "[a-zA-z_][a-zA-z0-9_]*";
    public static final String LEGAL_GROUP_NAME_REGEX = "[a-zA-z0-9_][a-zA-z0-9_\\-]*";

    public static boolean isLegalCommandName(@NotNull String commandName) {
        return commandName.matches(LEGAL_COMMAND_NAME_REGEX);
    }

    public static boolean isLegalVariableName(@NotNull String variableName) {
        return variableName.matches(LEGAL_VARIABLE_NAME_REGEX);
    }

    public static boolean isLegalGroupName(@NotNull String variableName) {
        return variableName.matches(LEGAL_GROUP_NAME_REGEX);
    }

}
