package org.taixue.customcommands.script;

import org.bukkit.command.CommandSender;
import org.taixue.customcommands.language.Messages;

public class MessageScript extends Script {
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
        commandSender.sendMessage(Messages.replaceVariableString(message));
    }
}
