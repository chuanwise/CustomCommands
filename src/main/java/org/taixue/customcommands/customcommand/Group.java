package org.taixue.customcommands.customcommand;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Group {
    private String name;
    private ArrayList<Command> commands = new ArrayList<>();

    public Group() {
        this("");
    }

    public Group(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
        for (Command command : commands) {
            command.setUsageToDefault();
        }
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    /**
     * 将一个指令加入当前的指令组中
     * 
     * @param command
     */
    public void addCommand(@NotNull Command command) {
        commands.add(command);
        command.setGroup(this);
    }

    /**
     * 通过参数查找可匹配的指令列表
     * 
     * @param arguments 参数列表
     * @return 可与之匹配的指令列表
     */
    @Nullable
    public ArrayList<Command> getCommands(@NotNull String[] arguments) {
        if (arguments.length == 0 || !arguments[0].equals(name)) {
            return null;
        }
        ArrayList<Command> result = new ArrayList<>();
        for (Command command : commands) {
            if (command.isLegalArguments(arguments)) {
                result.add(command);
            }
        }
        return result;
    }

    /**
     * 通过指令名查找指令
     * 
     * @param commandName
     * @return
     */
    @Nullable
    public Command getCommand(@NotNull String commandName) {
        for (Command command : commands) {
            if (command.getName().equals(commandName)) {
                return command;
            }
        }
        return null;
    }

    public void removeCommand(@NotNull String commandName) {
        commands.remove(getCommand(commandName));
    }
}
