package io.github.taixue.plugin.customcommands.language;

import com.alibaba.fastjson.JSON;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.path.Paths;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

// waiting for finish
public class Messages {
    private Messages() {}

    private static Language language;
    private static Logger logger;

    private static Map<String, String> environment = new HashMap<>();

    public static boolean setLanguage(@NotNull String languageCode) {
        try {
            language = JSON.parseObject(Plugin.plugin.getResource(Paths.LANG_DIR + Paths.SPLIT + languageCode + ".json"),
                    Language.class);
            return true;
        }
        catch (Exception exception) {
            severeString("Fail to load language file: " + language + ".json " + ", because: " + exception);
            exception.printStackTrace();
            return false;
        }
    }

    public static Language getLanguage() {
        return language;
    }

    public static void setLogger(@NotNull Logger logger) {
        Messages.logger = logger;
    }

    public static void setException(@NotNull Exception exception) {
        setVariable("exception", exception.getClass().getName());
        setVariable("exceptionMessage", exception.getMessage());
    }

    public static void setVariable(@NotNull String variableName, @NotNull String value) {
        environment.put(variableName, value);
    }

    public static void setVariable(@NotNull String variableName, int value) {
        setVariable(variableName, Integer.toString(value));
    }

    public static void setNewVariable(@NotNull String variableName, @NotNull String value) {
        clearVariables();
        setVariable(variableName, value);
    }

    public static boolean containsVariable(@NotNull String variableName) {
        return environment.containsKey(variableName);
    }

    @Nullable
    public static String getVariable(@NotNull String key) {
        return environment.getOrDefault(key, null);
    }

    public static void clearVariables() {
        environment.clear();
    }

    @NotNull
    public static String replaceVariableString(@NotNull String string) {
        if (!string.contains("{")) {
            return string;
        }
        StringBuilder stringBuilder = new StringBuilder(string);
        int lparen = 0, rparen = 0;

        while (stringBuilder.indexOf("{") != -1) {
            lparen = stringBuilder.indexOf("{", rparen);
            rparen = stringBuilder.indexOf("}", lparen);

            if (lparen != -1 &&
                    rparen != -1 &&
                    rparen > lparen + 1) {
                String variableValue = getVariable(stringBuilder.substring(lparen + 1, rparen));
                if (Objects.nonNull(variableValue)) {
                    stringBuilder.replace(lparen, rparen + 1, variableValue);
                }
            }
            else {
                break;
            }
        }
        return stringBuilder.toString();
    }

    @NotNull
    public static String replaceVariableLanguage(@NotNull String messageName) {
        try {
            Field message = Language.class.getField(messageName);
            return replaceVariableString(((String) message.get(language)));
        }
        catch (Exception noSuchFieldException) {
            return messageName;
        }
    }

    public static void sendMessageString(@NotNull CommandSender sender, String message) {
        if (Objects.isNull(language)) {
            sender.sendMessage("Unexpected error: cannot load language file, please check if syntax errors exist.");
            sender.sendMessage("[" + Plugin.NAME + "]" + message);
            severeString("Unexpected error: cannot load language file, please check if syntax errors exist.");
        }
        else {
            sender.sendMessage(language.messageHead + message);
        }
    }

    public static void sendMessageStringAndLog(@NotNull CommandSender sender, String message) {
        sendMessage(sender, message);
        infoString(sender.getName() + " << " + message);
    }

    public static void sendMessage(@NotNull CommandSender sender, String messageName) {
        sendMessageString(sender, replaceVariableLanguage(messageName));
    }

    public static void sendMessageAndLog(@NotNull CommandSender sender, String messageName) {
        sendMessageStringAndLog(sender, replaceVariableLanguage(messageName));
    }

    public static void infoString(@NotNull String string) {
        logger.info(replaceVariableString(string));
    }

    public static void warningString(@NotNull String string) {
        logger.warning(replaceVariableString(string));
    }

    public static void severeString(@NotNull String string) {
        logger.severe(replaceVariableString(string));
    }

    public static void infoLang(@NotNull String messageName) {
        infoString(replaceVariableLanguage(messageName));
    }

    public static void warningLang(@NotNull String messageName) {
        warningString(replaceVariableLanguage(messageName));
    }

    public static void severeLanguage(@NotNull String messageName) {
        severeString(replaceVariableLanguage(messageName));
    }

    public static void debugString(@NotNull CommandSender sender, @NotNull String string) {
        sender.sendMessage(language.debugHead + replaceVariableString(string));
    }

    public static void debugLang(@NotNull CommandSender sender, @NotNull String messageName) {
        sender.sendMessage(language.debugHead + replaceVariableLanguage(messageName));
    }
}
