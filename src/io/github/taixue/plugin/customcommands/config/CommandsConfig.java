package io.github.taixue.plugin.customcommands.config;

import com.sun.istack.internal.NotNull;
import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Formatter;
import io.github.taixue.plugin.customcommands.util.Groups;
import io.github.taixue.plugin.customcommands.util.Strings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommandsConfig extends Config {

    private ArrayList<Group> groups = new ArrayList<>();

    public CommandsConfig(File file, String head) {
        super(file, head);
        load();
    }

    public CommandsConfig() {
        this(new File(Plugin.plugin.getDataFolder(), "commands.yml"), "commands");
    }

    public void load() {
        Formatter.setNewVariable("file", file.getName());
        try {
            Map<String, Object> groupsMap = configSection.getValues(false);
            Set<String> loadedGroups = new HashSet<>();
            for (String groupName: groupsMap.keySet()) {
                Formatter.setVariable("group", groupName);
                if (loadedGroups.contains(groupName)) {
                    Formatter.severeLanguage("redefinedGroups");
                }
                else {
                    if (Strings.isLegalGroupName(groupName)) {
                        loadedGroups.add(groupName);
                        try {
                            addGroup(Groups.loadFromMemorySection(((MemorySection) groupsMap.get(groupName))));
                        } catch (ClassCastException classCastException) {
                            Formatter.severeLanguage("wrongFormatForGroup");
                        } catch (Exception exception) {
                            Formatter.setException(exception);
                            Formatter.severeLanguage("exceptionInLoadingGroup");
                            exception.printStackTrace();
                        }

                    }
                    else {
                        Formatter.severeLanguage("illegalGroupName");
                    }
                }
            }
        }
        catch (Exception exception) {
            Formatter.setException(exception);
            Formatter.severeLanguage("exceptionInLoadingFile");
            exception.printStackTrace();
        }
    }

    @NotNull
    public ArrayList<Command> getCommands(String[] arguments) {
        if (arguments.length == 0) {
            return null;
        }
        Group group = getGroup(arguments[0]);
        return group.getCommands(arguments);
    }

    /**
     * 通过组名查找指令组
     * @param groupName 组名
     * @return          指令组
     */
    public Group getGroup(String groupName) {
        for (Group group: groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    public void reload() {
        groups.clear();
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        configSection = ((MemorySection) fileConfiguration.get("commands"));
        load();
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void removeGroup(String groupName) {
        groups.remove(getGroup(groupName));
    }

    @Override
    public void save() throws IOException {
//        fileConfiguration.set("commands", null);
        configSection = fileConfiguration.createSection("commands");
        for (Group group: groups) {
            ConfigurationSection groupSection = configSection.createSection(group.getName());
            for (Command command: group.getCommands()) {
                ConfigurationSection commandSection = groupSection.createSection(command.getName());
                commandSection.set("format", command.getFormat());
                commandSection.set("actions", command.getActions());

                if (!isDefaultUsage(command)) {
                    commandSection.set("usage", command.getUsageString());
                }
//                commandSection.set("matches", command.getMatches());

                if (!isDefaultIdentify(command)) {
                    commandSection.set("identify", command.getIdentifyString());
                }

                if (!isDefaultResult(command)) {
                    commandSection.set("result", command.getResultString());
                }

                if (!isDefaultPermissions(command)) {
                    commandSection.set("permissions", command.getPermissions());
                }
            }
        }
        super.save();
    }

    public boolean isDefaultUsage(Command command) {
        return command.getUsageString().equals("/ccsr " +
                command.getGroup().getName() + " " +
                command.getFormat());
    }

    public boolean isDefaultIdentify(Command command) {
        return command.getIdentify() == Command.Identify.AUTO;
    }

    public boolean isDefaultPermissions(Command command) {
        return command.getPermissions().length == 1 &&
                command.getPermissions()[0].equals("ccs.run." + command.getGroup().getName() + "." + command.getName());
    }

    public boolean isDefaultResult(Command command) {
        return command.getResultString().equals(Formatter.getVariable("defaultResultString"));
    }
}
