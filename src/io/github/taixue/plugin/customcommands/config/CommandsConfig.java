package io.github.taixue.plugin.customcommands.config;

import com.sun.istack.internal.NotNull;
import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.customcommand.Command;
import io.github.taixue.plugin.customcommands.customcommand.Group;
import io.github.taixue.plugin.customcommands.language.Messages;
import io.github.taixue.plugin.customcommands.util.Groups;
import io.github.taixue.plugin.customcommands.util.Strings;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Messages.setNewVariable("file", file.getName());
        try {
            Map<String, Object> groupsMap = configSection.getValues(false);
            Set<String> loadedGroups = new HashSet<>();
            for (String groupName: groupsMap.keySet()) {
                Messages.setVariable("group", groupName);
                if (Strings.isLegalGroupName(groupName)) {
                    if (loadedGroups.contains(groupName)) {
                        Messages.severeLanguage("illegalGroupName");
                    }
                    else {
                        loadedGroups.add(groupName);
                        try {
                            addGroup(Groups.loadFromMemorySection(((MemorySection) groupsMap.get(groupName))));
                        } catch (ClassCastException classCastException) {
                            Messages.severeLanguage("wrongFormatForGroup");
                        } catch (Exception exception) {
                            Messages.setException(exception);
                            Messages.severeLanguage("exceptionInLoadingGroup");
                        }
                    }
                }
                else {
                    Messages.severeLanguage("illegalGroupName");
                }
            }
        }
        catch (Exception exception) {
            Messages.setException(exception);
            Messages.severeLanguage("exceptionInLoadingFile");
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
}
