package org.taixue.customcommands.script;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.taixue.customcommands.language.Messages;
import org.taixue.customcommands.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Scripts {
    private Scripts() {}

    @Nullable
    public static Script parseScript(@NotNull String string, @NotNull CommandSender sender) {
        Script result = null;
        String name;
        String[] arguments;

        if (string.contains(" ")) {
            name = string.substring(1, string.indexOf(" "));
            ArrayList<String> argumentsResult = new ArrayList<>();
            for (String argument: string.substring(name.length() + 1).trim().split("\\s")) {
                if (!argument.isEmpty()) {
                    argumentsResult.add(argument);
                }
            }
            arguments = argumentsResult.toArray(new String[0]);
        }
        else {
            name = string.substring(1);
            arguments = new String[0];
        }

        if (Plugin.debug) {
            Messages.debugString(sender, "script: " + string);
            Messages.debugString(sender, "name: @" + name);
            Messages.debugString(sender, "args: " + Arrays.toString(arguments));
        }

        Messages.setVariable("script", name);

        switch (name) {
            case "sleep":
                if (arguments.length == 1 && arguments[0].matches("\\d+")) {
                    SleepScript sleepScript = new SleepScript();
                    sleepScript.setTime(Integer.parseInt(arguments[0]));
                    result = sleepScript;
                }
                break;
            case "title":
                String mainTitle, subTitle;
                if (!(sender instanceof Player)) {
                    Messages.sendMessage(sender, "illegalScript");
                }
                else if (arguments.length != 0) {
                    mainTitle = arguments[0];
                    if (arguments.length >= 1) {
                        subTitle = Strings.getRemainString(arguments, 1);
                    }
                    else {
                        subTitle = null;
                    }
                    TitleScript titleScript = new TitleScript((Player) sender);
                    titleScript.setMainTitle(mainTitle);
                    titleScript.setSubTitle(subTitle);
                    result = titleScript;
                }
                break;
            case "message":
                if (arguments.length >= 1) {
                    String message = Strings.getRemainString(arguments, 0);
                    MessageScript messageScript = new MessageScript(sender);
                    messageScript.setMessage(message);
                    result = messageScript;
                }
                break;
            default:
                Messages.sendMessage(sender, "unknownScript");
                return null;
        }
        if (Objects.isNull(result)) {
            Messages.sendMessage(sender, "scriptSyntaxError");
        }
        return result;
    }
}
