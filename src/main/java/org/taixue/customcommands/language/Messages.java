package org.taixue.customcommands.language;

import com.alibaba.fastjson.JSON;
import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.util.Paths;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class Messages {
    private Messages() {
    }

    private static Language language;
    private static Logger logger;

    private static HashMap<String, String> environment = new HashMap<>();

    public static boolean setLanguage(@NotNull String languageCode) {
        if (Objects.nonNull(language)) {
            return true;
        }
        try {
            language = JSON.parseObject(
                    Plugin.plugin.getResource(Paths.LANG_DIR + Paths.SPLIT + languageCode + ".json"), Language.class);
            return true;
        } catch (Exception exception) {
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

    @Nullable
    public static String getVariable(@NotNull String key) {
        return environment.getOrDefault(key, null);
    }

    public static HashMap<String, String> getEnvironment() {
        return environment;
    }

    public static void clearVariables() {
        environment.clear();
    }

    public static String replaceVariableString(@NotNull String string) {
        return replaceVariableString(null, string);
    }

    @NotNull
    public static String replaceVariableString(Player player, @NotNull String string) {
        if (!string.contains("{")) {
            return string;
        }
        StringBuilder stringBuilder = new StringBuilder(string);
        int lparen = 0, rparen = 0;

        while (stringBuilder.indexOf("{") != -1) {
            lparen = stringBuilder.indexOf("{", rparen);
            rparen = stringBuilder.indexOf("}", lparen);

            if (lparen != -1 && rparen != -1 && rparen > lparen + 1) {
                String variableName = stringBuilder.substring(lparen + 1, rparen);
                String variableValue = Environment.getVariable(player, variableName);
                if (Objects.nonNull(variableValue)) {
                    if (variableName.contains("exception")) {
                        stringBuilder.replace(lparen, rparen + 1, red(variableValue));
                    } else {
                        stringBuilder.replace(lparen, rparen + 1, variableValue);
                    }
                }
            } else {
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
        } catch (Exception noSuchFieldException) {
            return messageName;
        }
    }

    public static void sendMessageString(@NotNull CommandSender sender, String message) {
        if (Objects.isNull(language)) {
            sender.sendMessage("Unexpected error: cannot load language file, please check if syntax errors exist.");
            sender.sendMessage("[" + Plugin.NAME + "]" + message);
            severeString("Unexpected error: cannot load language file, please check if syntax errors exist.");
        } else {
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
        sender.sendMessage(language.debugHead + string);
    }

    public static void hello() {
        infoString(yellow("Nice to meet you! o(*￣▽￣*)ブ"));
        infoString(purple("CustomCommands is written by Chuanwise, open source under GPL GNU license"));
        Messages.infoString(white("You can support us in following websites:"));
        Messages.infoString(yellow("Github: ") + Plugin.GITHUB + gray("(Remember to give me a star :>)"));
        Messages.infoString(yellow("MCBBS: ") + Plugin.MCBBS);
        Messages.infoString(
                "Join the QQ group: " + red(Plugin.QQ_GROUP) + " to get the newest update and some tech-suppositions.");
    }

    public static String white(String string) {
        return "\033[30;33m" + string + "\33[0m";
    }

    public static String red(String string) {
        return "\033[31;33m" + string + "\33[0m";
    }

    public static String green(String string) {
        return "\033[32;33m" + string + "\33[0m";
    }

    public static String yellow(String string) {
        return "\033[33;33m" + string + "\33[0m";
    }

    public static String blue(String string) {
        return "\033[34;33m" + string + "\33[0m";
    }

    public static String purple(String string) {
        return "\033[35;33m" + string + "\33[0m";
    }

    public static String cyan(String string) {
        return "\033[36;33m" + string + "\33[0m";
    }

    public static String gray(String string) {
        return "\033[36;33m" + string + "\33[0m";
    }
}
