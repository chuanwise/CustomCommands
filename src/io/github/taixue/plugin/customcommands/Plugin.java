package io.github.taixue.plugin.customcommands;

import io.github.taixue.plugin.customcommands.commandexecutor.CCSCCommandExecutor;
import io.github.taixue.plugin.customcommands.commandexecutor.CCSCommandExecutor;
import io.github.taixue.plugin.customcommands.commandexecutor.CCSRCommandExecutor;
import io.github.taixue.plugin.customcommands.config.CommandsConfig;
import io.github.taixue.plugin.customcommands.config.PluginConfig;
import io.github.taixue.plugin.customcommands.language.Messages;
import io.github.taixue.plugin.customcommands.path.Paths;
import io.github.taixue.plugin.customcommands.util.Files;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;


public class Plugin {
    private Plugin() {}

    public static final String VERSION = "2.0";
    public static final String NAME = "CustomCommands";
    public static final String AUTHOR = "Chuanwise";
    public static final String ORGANIZATION = "Taixue";
    public static final String GITHUB = "https://github.com/Chuanwise/CustomCommands";
    public static final String QQ_GROUP = "1028582500";

    public static CustomCommandPlugin plugin;

    public static PluginConfig pluginConfig;
    public static CommandsConfig commandsConfig;

    public static boolean debug = false;

    public static final CCSCommandExecutor CCS_COMMAND_EXECUTOR = new CCSCommandExecutor();
    public static final CCSCCommandExecutor CCSC_COMMAND_EXECUTOR = new CCSCCommandExecutor();
    public static final CCSRCommandExecutor CCSR_COMMAND_EXECUTOR = new CCSRCommandExecutor();

    private static File configFile;
    private static File commandsFile;

    public static void load(CustomCommandPlugin customCommandPlugin) {
        plugin = customCommandPlugin;
        Messages.setLogger(plugin.getLogger());
        setCommandExecutors();

        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            Messages.severeString("Directory " + plugin.getDataFolder().getName() + " doesn't exist and cannot be created!");
            return;
        }

        if (!checkFrontPlugins()) {
            Messages.severeString("Lack some front plugins, can not load" + NAME + ".");
            return;
        }

        configFile = new File(plugin.getDataFolder(), Paths.CONFIG);
        commandsFile = new File(plugin.getDataFolder(), Paths.COMMANDS);

        Messages.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Messages.infoString("Hello! Nice to meet you!");
        Messages.infoString("CustomCommands is written by Chuanwise, all rights reserved by Chuanwise and Taixue.");
        Messages.infoString("You can support us in " + GITHUB + " :)");
        Messages.infoString("Join the QQ group: " + QQ_GROUP + " to get the newest update and some tech-suppositions.");
        Messages.infoString("---------- loading ----------");
        Messages.infoString("loading config...");
        loadConfig();
        Messages.infoString("loading language...");
        loadMessage();
        Messages.infoString("loading commands...");
        loadCommands();
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

    private static void loadConfig() {
        try {
            if (!configFile.exists()) {
                plugin.saveDefaultConfig();
            }
            pluginConfig = new PluginConfig(configFile, "config");
            debug = ((Boolean) pluginConfig.get("debug", false));
        }
        catch (YAMLException exception) {
            exception.printStackTrace();
        }
    }

    private static boolean loadMessage() {
        Messages.setLogger(Plugin.plugin.getLogger());
        return Messages.setLanguage(((String) pluginConfig.get("lang", "en")));
    }

    private static void loadCommands() {
        if (!commandsFile.exists()) {
            if (!Files.fileCopy(Paths.COMMANDS, commandsFile)) {
                Messages.severeString("cannot create the default commands.yml");
            }
        }
        commandsConfig = new CommandsConfig();
    }

    public static void saveConfig() {
        try {
            pluginConfig.save();
        }
        catch (IOException ioException) {
            Messages.severeString(ioException + " at saving config.yml");
        }
    }

    public static void saveCommands() {
        try {
            commandsConfig.save();
        }
        catch (IOException ioException) {
            Messages.severeString(ioException + " at saving commands.yml");
        }
    }

    public static void close() {
        Messages.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Messages.infoString("saving config.yml");
        saveConfig();
        Messages.infoString("saving commands.yml");
        saveCommands();
        Messages.infoString("------ all configuration saved ------");

        Messages.infoString("CustomCommands is written by Chuanwise, all rights reserved by Chuanwise and Taixue.");
        Messages.infoString("You can support us in " + GITHUB + " :)");
        Messages.infoString("Join the QQ group: " + QQ_GROUP + " to get the newest update and some tech-suppositions.");
        Messages.infoString("------ Think you for using CustomCommands, see you :) ------");
    }

    private static void setCommandExecutors() {
        plugin.getCommand("customcommandsrun").setExecutor(CCSR_COMMAND_EXECUTOR);
        plugin.getCommand("customcommandsconfig").setExecutor(CCSC_COMMAND_EXECUTOR);
        plugin.getCommand("customcommands").setExecutor(CCS_COMMAND_EXECUTOR);
    }
}
