package org.taixue.customcommands.language;

import com.sun.istack.internal.Nullable;
import org.taixue.customcommands.Plugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Environment {
    private static final File directory = new File(Plugin.plugin.getDataFolder(), "environment");
    // Player null means 'global'
    private static final Map<Player, Map<String, String>> environment = new HashMap<>();

    public Environment() {
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        }
    }

    public static boolean containsPlayer(Player player) {
        for (Player p: environment.keySet()) {
            if (Objects.isNull(p)) {
                continue;
            }
            if (p.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public static void put(Player player, String variableName, String value) {
        Map<String, String> playerEnvironment = getPlayerEnvironment(player);
        if (Objects.isNull(playerEnvironment)) {
            playerEnvironment = new HashMap<>();
            environment.put(player, playerEnvironment);
        }
        playerEnvironment.put(variableName, value);
        saveEnvironment(player);
    }

    public static boolean remove(Player player, String variableName) {
        Map<String, String> environment = getPlayerEnvironment(player);
        if (Objects.isNull(environment) || !environment.containsKey(variableName)) {
            return false;
        }
        environment.remove(variableName);
        saveEnvironment(player);
        return true;
    }

    public static void clear(Player player) {
        if (containsPlayer(player)) {
            environment.put(player, new HashMap<>());
            saveEnvironment(player);
        }
    }

    public static String get(Player player, String key) {
        if (containsPlayer(player)) {
            return environment.get(player).getOrDefault(key, null);
        }
        else {
            return null;
        }
    }

    @Nullable
    public static String getVariable(Player player, String key) {
        String value = null;
        if (Objects.nonNull(player)) {
            value = get(player, key);
        }
        if (Objects.isNull(value)) {
            value = get(null, key);
        }
        return Objects.isNull(value) ? Messages.getVariable(key) : value;
    }

    public static boolean load(Player player) {
        return load(player, getPlayerEnvironmentFile(player));
    }

    public static boolean load(Player player, File file) {
        try {
            Messages.setVariable("file", file.getName());
            ConfigurationSection root = ((ConfigurationSection) YamlConfiguration.loadConfiguration(file).get("environment"));
            if (Objects.isNull(root)) {
                return false;
            }
            Map<String, Object> currentMap = root.getValues(false);
            for (Object object: currentMap.values()) {
                if (!(object instanceof String)) {
                    Messages.severeLanguage("variableShouldBeString");
                    return false;
                }
            }
            environment.put(player,((Map<String, String>) (Object) currentMap));
            return true;
        }
        catch (Exception exception) {
            Messages.setException(exception);
            Messages.severeLanguage("exceptionInLoadingEnvironment");
            exception.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static Map<String, String> getPlayerEnvironment(@Nullable Player player) {
        return environment.getOrDefault(player, null);
    }

    private static File getPlayerEnvironmentFile(@Nullable Player player) {
        return new File(directory, (Objects.isNull(player) ? "global" : player.getUniqueId().toString()) + ".yml");
    }

    public static boolean saveEnvironment(@Nullable Player player) {
        return saveEnvironment(getPlayerEnvironmentFile(player), environment.get(player));
    }

    public static boolean saveEnvironment(File saveTo, Map<String, String> environment) {
        Messages.setVariable("UUID", saveTo.getName());
        if (!saveTo.exists()) {
            try {
                saveTo.createNewFile();
            }
            catch (IOException ioException) {
                Messages.setException(ioException);
                Messages.severeLanguage("exceptionInSavingEnvironment");
                ioException.printStackTrace();
                return false;
            }
        }
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("environment", environment);
            yaml.save(saveTo);
            return true;
        }
        catch (Exception exception) {
            Messages.setException(exception);
            Messages.severeLanguage("exceptionInSavingEnvironment");
            exception.printStackTrace();
            return false;
        }
    }

    public static void clear() {
        environment.clear();
    }

    public static void unload(Player player) {
        environment.remove(player);
    }
}
