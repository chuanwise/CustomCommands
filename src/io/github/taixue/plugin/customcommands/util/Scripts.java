package io.github.taixue.plugin.customcommands.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.github.taixue.plugin.customcommands.customcommand.Script;
import io.github.taixue.plugin.customcommands.customcommand.SleepScript;

public class Scripts {
    private Scripts() {}

    @Nullable
    public static Script parseScript(@NotNull String string) {
        if (!string.startsWith("@")) {
            return null;
        }
        Script result;
        String name;
        String[] arguments;
        if (string.contains(" ")) {
            name = string.substring(1, string.indexOf(" "));
            arguments = string.substring(name.length()).split("\\s", 0);
        }
        else {
            name = string.substring(1);
            arguments = new String[0];
        }
        if (name.equalsIgnoreCase("sleep")) {
            if (arguments.length == 1 && arguments[0].matches("\\d+")) {
                result = new SleepScript();
                ((SleepScript) result).setTime(Integer.parseInt(arguments[0]));
            }
        }
//        else if (name.equalsIgnoreCase(""))
        return null;
    }
}
