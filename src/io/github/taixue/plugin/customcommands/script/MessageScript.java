package io.github.taixue.plugin.customcommands.script;

import io.github.taixue.plugin.customcommands.language.Formatter;
import org.bukkit.command.CommandSender;

public class MessageScript extends Script{
    private CommandSender commandSender;
    private String message;

    public MessageScript(CommandSender commandSender) {
        setCommandSender(commandSender);
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void run() {
        commandSender.sendMessage(Formatter.replaceVariableString(message));
    }
}
