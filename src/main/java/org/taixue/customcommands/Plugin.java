package org.taixue.customcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.taixue.customcommands.commandexecutor.CCSCCommandExecutor;
import org.taixue.customcommands.commandexecutor.CCSCommandExecutor;
import org.taixue.customcommands.commandexecutor.CCSECommandExecutor;
import org.taixue.customcommands.commandexecutor.CCSRCommandExecutor;
import org.taixue.customcommands.config.CommandsConfig;
import org.taixue.customcommands.config.PluginConfig;
import org.taixue.customcommands.language.Environment;
import org.taixue.customcommands.language.Messages;
import org.taixue.customcommands.listener.PlayerJoinListener;
import org.taixue.customcommands.listener.PlayerQuitListener;
import org.taixue.customcommands.util.Files;
import org.taixue.customcommands.util.Paths;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;

public class Plugin extends JavaPlugin {
    public static final String VERSION = "4.1";
    public static final String NAME = "CustomCommands";
    public static final String AUTHOR = "Chuanwise";
    public static final String ORGANIZATION = "Taixue";
    public static final String GITHUB = "https://github.com/Chuanwise/CustomCommands";
    public static final String MCBBS = "https://www.mcbbs.net/thread-1172706-1-1.html";
    public static final String QQ_GROUP = "1028582500";

    public static Plugin plugin;

    public static PluginConfig pluginConfig;
    public static CommandsConfig commandsConfig;

    public static boolean debug = false;
    public static boolean autoSave = true;
    public static int waitForUnload = 5 * 60000;

    public static final CCSCommandExecutor CCS_COMMAND_EXECUTOR = new CCSCommandExecutor();
    public static final CCSCCommandExecutor CCSC_COMMAND_EXECUTOR = new CCSCCommandExecutor();
    public static final CCSRCommandExecutor CCSR_COMMAND_EXECUTOR = new CCSRCommandExecutor();
    public static final CCSECommandExecutor CCSE_COMMAND_EXECUTOR = new CCSECommandExecutor();

    public static final PlayerJoinListener PLAYER_JOIN_LISTENER = new PlayerJoinListener();
    public static final PlayerQuitListener PLAYER_QUIT_LISTENER = new PlayerQuitListener();

    private static File configFile;
    private static File commandsFile;

    public static void load(Plugin plugin) {
        Plugin.plugin = plugin;
        Messages.setLogger(Plugin.plugin.getLogger());
        setCommandExecutors();

        if (!Plugin.plugin.getDataFolder().exists() && !Plugin.plugin.getDataFolder().mkdirs()) {
            Messages.severeString("Directory " + Plugin.plugin.getDataFolder().getName() + " doesn't exist and cannot be created!");
            return;
        }

        if (!checkFrontPlugins()) {
            Messages.severeString("Lack some front plugins, can not load" + NAME + ".");
            return;
        }

        configFile = new File(Plugin.plugin.getDataFolder(), Paths.CONFIG);
        commandsFile = new File(Plugin.plugin.getDataFolder(), Paths.COMMANDS);

        Messages.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Messages.hello();
        Messages.infoString("---------- loading ----------");
        Messages.infoString(Messages.blue("loading config..."));
        loadConfig();
        Messages.infoString(Messages.blue("loading language..."));
        loadLanguage();
        Messages.infoString(Messages.blue("loading commands..."));
        loadCommands();
//        Messages.infoString(Messages.blue("registing events..."));
//        registerEvents();
        reloadEnvironment();
        Messages.infoString("------ load configurations completely :) ------");
    }

    private static boolean checkFrontPlugins() {
//        org.bukkit.plugin.Plugin plugin =
//                Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
//        if (plugin != null){
//            System.out.println("这个服务器安装了PAPI插件！");
//        }
        return true;
    }

    public static void reload() {
        load(plugin);
    }

    public static void loadConfig() {
        try {
            if (!configFile.exists()) {
                plugin.saveDefaultConfig();
            }
            pluginConfig = new PluginConfig(configFile, "config");
            debug = ((Boolean) pluginConfig.get("debug", false));
            autoSave = ((Boolean) pluginConfig.get("auto-save", true));
            waitForUnload = ((Integer) pluginConfig.get("wait-for-unload", 5 * 60000));
        }
        catch (YAMLException exception) {
            exception.printStackTrace();
        }
    }

    private static boolean loadLanguage() {
        Messages.setLogger(Plugin.plugin.getLogger());
        return Messages.setLanguage(((String) pluginConfig.get("lang", "en")));
    }

    public static void loadCommands() {
        if (!commandsFile.exists()) {
            if (!Files.fileCopy(Paths.COMMANDS, commandsFile)) {
                Messages.severeString("cannot create the default commands.yml");
            }
        }
        commandsConfig = new CommandsConfig();
    }

    public static void saveConfigFile() {
        try {
            pluginConfig.save();
        }
        catch (Exception exception) {
            Messages.severeString(exception + " at saving config.yml");
            exception.printStackTrace();
        }
    }

    public static void saveCommands() {
        try {
            commandsConfig.save();
        }
        catch (Exception exception) {
            Messages.severeString(exception + " at saving commands.yml");
            exception.printStackTrace();
        }
    }

    public static void close() {
        PLAYER_QUIT_LISTENER.shutdown();
        Messages.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Messages.infoString("\033[1;33msaving config.yml \033[0m");
        saveConfigFile();
        Messages.infoString("\033[1;33msaving commands.yml \033[0m");
        saveCommands();
        Messages.infoString("------ all configuration saved ------");
        Messages.hello();
        Messages.infoString("------ Think you for using CustomCommands, see you :) ------");
    }

    private static void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(PLAYER_JOIN_LISTENER, plugin);
        plugin.getServer().getPluginManager().registerEvents(PLAYER_QUIT_LISTENER, plugin);
    }

    public static void loadOnlinePlayerEnvironment() {
        for (Player player: plugin.getServer().getOnlinePlayers()) {
            Messages.infoString(Messages.yellow("loading player: " + player.getName() + "'s environment..."));
            PLAYER_JOIN_LISTENER.loadPlayerEnvironment(player);
        }
    }

    public static void loadGlobalEnvironment() {
        Messages.infoString(Messages.yellow("loading global environment..."));
        PLAYER_JOIN_LISTENER.loadPlayerEnvironment(null);
    }

    private static void setCommandExecutors() {
        plugin.getCommand("customcommandsrun").setExecutor(CCSR_COMMAND_EXECUTOR);
        plugin.getCommand("customcommandsconfig").setExecutor(CCSC_COMMAND_EXECUTOR);
        plugin.getCommand("customcommands").setExecutor(CCS_COMMAND_EXECUTOR);
        plugin.getCommand("customcommandsenvironment").setExecutor(CCSE_COMMAND_EXECUTOR);
    }

    public static void reloadEnvironment() {
        Environment.clear();
        Messages.infoString(Messages.blue("loading global environments..."));
        loadGlobalEnvironment();
        Messages.infoString(Messages.blue("loading online players' environments..."));
        loadOnlinePlayerEnvironment();
    }

    @Override
    public void onEnable() {
        load(this);
    }

    @Override
    public void onDisable() {
        close();
    }
}