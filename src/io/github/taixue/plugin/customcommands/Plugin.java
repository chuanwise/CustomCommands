package io.github.taixue.plugin.customcommands;

import io.github.taixue.plugin.customcommands.command.CCSCCommand;
import io.github.taixue.plugin.customcommands.command.CCSCommand;
import io.github.taixue.plugin.customcommands.command.CCSRCommand;
import io.github.taixue.plugin.customcommands.lang.Language;
import io.github.taixue.plugin.customcommands.path.Path;
import io.github.taixue.plugin.customcommands.util.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class Plugin {
    private Plugin() {}

    public static final String VERSION = "1";
    public static final String NAME = "CustomCommands";
    public static final String GITHUB = "https://github.com/Chuanwise/CustomCommands";
    public static final String QQ_GROUP = "1028582500";

    public static CustomCommandPlugin plugin;
    public static Language language;
    public static Logger logger;

    public static FileConfiguration configuration;

    public static boolean debug = false;

    public static final CCSCommand ccsCommand = new CCSCommand();
    public static final CCSCCommand ccscCommand = new CCSCCommand();
    public static final CCSRCommand ccsrCommand = new CCSRCommand();

    private static File configFile;
    private static File commandsFile;

    public static void load(CustomCommandPlugin customCommandPlugin) {
        plugin = customCommandPlugin;
        logger = plugin.getLogger();
        setCommandExecutors();

        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            logger.severe("Directory " + plugin.getDataFolder().getName() + " doesn't exist and cannot be created!");
            return;
        }

        configFile = new File(plugin.getDataFolder(), Path.CONFIG);
        commandsFile = new File(plugin.getDataFolder(), Path.COMMANDS);

        logger.info("----------[" + NAME + " - " + VERSION +"]----------");

        logger.info("Hello! Nice to meet you!");

        logger.info("CustomCommands is written by Chuanwise, all rights reserved by Chuanwise and Taixue.");
        logger.info("You can support us in " + GITHUB + " :)");

        logger.info("Join the QQ group: " + QQ_GROUP + " to get the newest update and some tech-suppositions.");

        logger.info("---------- loading ----------");

        logger.info("loading config...");
        loadConfig();
        logger.info("loading language...");
        loadLanguage();
        logger.info("loading commands...");
        loadCommands();
        logger.info("------ load configurations completely :) ------");
    }

    public static void reload() {
        load(plugin);
    }

    private static void loadConfig() {
        try {
            if (!configFile.exists()) {
                plugin.saveDefaultConfig();
                if (!configFile.exists()) {
                    logger.severe("Fail to copy config.yml from jar to data folder for CustomCommands!");
                } else {
                    configuration = YamlConfiguration.loadConfiguration(configFile);
                }
            } else {
                configuration = plugin.getConfig();
            }
            debug = ((Boolean) configuration.get("config.debug", false));
        }
        catch (YAMLException exception) {
            exception.printStackTrace();
        }
    }

    private static void loadLanguage() {
        logger.info("language: " + configuration.get("config.lang"));
        logger.info("Language function is unfinished, default language is English.");
        language = new Language();
    }

    private static void loadCommands() {
        if (!commandsFile.exists()) {
            if (!FileUtil.fileCopy(Path.DEFAULT_COMMANDS, commandsFile)) {
                logger.severe("cannot create the default commands.yml");
            }
        }
        ccsrCommand.loadSavedCommand();
    }

    public static void saveConfig() {
        try {
            configuration.save(configFile);
        }
        catch (IOException ioException) {
            logger.severe(ioException + " at saving config.yml");
        }
    }

    public static void saveCommands() {
        try {
            ccsrCommand.getFileConfiguration().save(commandsFile);
        }
        catch (IOException ioException) {
            logger.severe(ioException + " at saving commands.yml");
        }
    }

    public static void close() {
        logger.info("----------[CustomCommands]----------");
        logger.info("saving config.yml");
        saveConfig();
        logger.info("saving commands.yml");
        saveCommands();
        logger.info("------ all configuration saved ------");

        logger.info("CustomCommands is written by Chuanwise, all rights reserved by Chuanwise and Taixue.");
        logger.info("You can support us in " + GITHUB + " :)");
        logger.info("Join the QQ group: " + QQ_GROUP + " to get the newest update and some tech-suppositions.");
        logger.info("------ Think you for using CustomCommands, see you :) ------");
    }

    private static void setCommandExecutors() {
        plugin.getCommand("customcommandsrun").setExecutor(ccsrCommand);
        plugin.getCommand("customcommandsconfig").setExecutor(ccscCommand);
        plugin.getCommand("customcommands").setExecutor(ccsCommand);
    }
}
