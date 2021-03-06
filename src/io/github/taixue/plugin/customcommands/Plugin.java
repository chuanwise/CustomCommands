package io.github.taixue.plugin.customcommands;

import io.github.taixue.plugin.customcommands.commandexecutor.CCSCCommandExecutor;
import io.github.taixue.plugin.customcommands.commandexecutor.CCSCommandExecutor;
import io.github.taixue.plugin.customcommands.commandexecutor.CCSECommandExecutor;
import io.github.taixue.plugin.customcommands.commandexecutor.CCSRCommandExecutor;
import io.github.taixue.plugin.customcommands.config.CommandsConfig;
import io.github.taixue.plugin.customcommands.config.PluginConfig;
import io.github.taixue.plugin.customcommands.language.Environment;
import io.github.taixue.plugin.customcommands.language.Formatter;
import io.github.taixue.plugin.customcommands.listener.PlayerJoinListener;
import io.github.taixue.plugin.customcommands.path.Paths;
import io.github.taixue.plugin.customcommands.util.Files;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;


public class Plugin {
    private Plugin() {}

    public static final String VERSION = "3.1";
    public static final String NAME = "CustomCommands";
    public static final String AUTHOR = "Chuanwise";
    public static final String ORGANIZATION = "Taixue";
    public static final String GITHUB = "https://github.com/Chuanwise/CustomCommands";
    public static final String MCBBS = "https://www.mcbbs.net/thread-1172706-1-1.html";
    public static final String QQ_GROUP = "1028582500";

    public static CustomCommandPlugin plugin;

    public static PluginConfig pluginConfig;
    public static CommandsConfig commandsConfig;

    public static boolean debug = false;
    public static boolean autoSave = true;

    public static final CCSCommandExecutor CCS_COMMAND_EXECUTOR = new CCSCommandExecutor();
    public static final CCSCCommandExecutor CCSC_COMMAND_EXECUTOR = new CCSCCommandExecutor();
    public static final CCSRCommandExecutor CCSR_COMMAND_EXECUTOR = new CCSRCommandExecutor();
    public static final CCSECommandExecutor CCSE_COMMAND_EXECUTOR = new CCSECommandExecutor();

    public static final PlayerJoinListener PLAYER_JOIN_LISTENER = new PlayerJoinListener();

    private static File configFile;
    private static File commandsFile;

    public static void load(CustomCommandPlugin customCommandPlugin) {
        plugin = customCommandPlugin;
        Formatter.setLogger(plugin.getLogger());
        setCommandExecutors();

        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            Formatter.severeString("Directory " + plugin.getDataFolder().getName() + " doesn't exist and cannot be created!");
            return;
        }

        if (!checkFrontPlugins()) {
            Formatter.severeString("Lack some front plugins, can not load" + NAME + ".");
            return;
        }

        configFile = new File(plugin.getDataFolder(), Paths.CONFIG);
        commandsFile = new File(plugin.getDataFolder(), Paths.COMMANDS);

        Formatter.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Formatter.hello();
        Formatter.infoString("---------- loading ----------");
        Formatter.infoString(Formatter.blue("loading config..."));
        loadConfig();
        Formatter.infoString(Formatter.blue("loading language..."));
        loadLanguage();
        Formatter.infoString(Formatter.blue("loading commands..."));
        loadCommands();
        Formatter.infoString(Formatter.blue("registing events..."));
        registerEvents();
        reloadEnvironment();
        Formatter.infoString("------ load configurations completely :) ------");
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

    private static void loadConfig() {
        try {
            if (!configFile.exists()) {
                plugin.saveDefaultConfig();
            }
            pluginConfig = new PluginConfig(configFile, "config");
            debug = ((Boolean) pluginConfig.get("debug", false));
            autoSave = ((Boolean) pluginConfig.get("auto-save", true));
        }
        catch (YAMLException exception) {
            exception.printStackTrace();
        }
    }

    private static boolean loadLanguage() {
        Formatter.setLogger(Plugin.plugin.getLogger());
        return Formatter.setLanguage(((String) pluginConfig.get("lang", "en")));
    }

    private static void loadCommands() {
        if (!commandsFile.exists()) {
            if (!Files.fileCopy(Paths.COMMANDS, commandsFile)) {
                Formatter.severeString("cannot create the default commands.yml");
            }
        }
        commandsConfig = new CommandsConfig();
    }

    public static void saveConfig() {
        try {
            pluginConfig.save();
        }
        catch (Exception exception) {
            Formatter.severeString(exception + " at saving config.yml");
            exception.printStackTrace();
        }
    }

    public static void saveCommands() {
        try {
            commandsConfig.save();
        }
        catch (Exception exception) {
            Formatter.severeString(exception + " at saving commands.yml");
            exception.printStackTrace();
        }
    }

    public static void close() {
        Formatter.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Formatter.infoString("\033[1;33msaving config.yml \033[0m");
        saveConfig();
        Formatter.infoString("\033[1;33msaving commands.yml \033[0m");
        saveCommands();
        Formatter.infoString("------ all configuration saved ------");
        Formatter.hello();
        Formatter.infoString("------ Think you for using CustomCommands, see you :) ------");
    }

    private static void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(PLAYER_JOIN_LISTENER, plugin);
    }

    public static void loadOnlinePlayerEnvironment() {
        for (Player player: plugin.getServer().getOnlinePlayers()) {
            Formatter.infoString(Formatter.yellow("loading player: " + player.getName() + "'s environment..."));
            PLAYER_JOIN_LISTENER.loadPlayerEnvironment(player);
        }
    }

    public static void loadGlobalEnvironment() {
        Formatter.infoString(Formatter.yellow("loading global environment..."));
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
        Formatter.infoString(Formatter.blue("loading global environments..."));
        loadGlobalEnvironment();
        Formatter.infoString(Formatter.blue("loading online players' environments..."));
        loadOnlinePlayerEnvironment();
    }
}
