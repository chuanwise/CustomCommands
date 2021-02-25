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


public class Plugin {
    private Plugin() {}

    public static final String VERSION = "3.0";
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
        Messages.infoString("\033[1;33mNice to meet you! \033[0m");
        Messages.infoString("\033[1;36mCustomCommands is written by Chuanwise, open source under GPL GNU license \033[0m");
        Messages.infoString("\033[1;36mYou can support us in following websites:  \033[0m");
        Messages.infoString("\033[1;36mGitHub: " + GITHUB + " \033[0m\033[1;33m (Remember to give me a star :> if you like this plugin) \033[0m");
        Messages.infoString("\033[1;36mMCBBS: " + MCBBS + " \033[0m");
        Messages.infoString("\033[1;36mJoin the QQ group: " + QQ_GROUP + " to get the newest update and some tech-suppositions. \033[0m");
        Messages.infoString("---------- loading ----------");
        Messages.infoString("\033[1;34mloading config... \033[0m");
        loadConfig();
        Messages.infoString("\033[1;34mloading language... \033[0m");
        loadLanguage();
        Messages.infoString("\033[1;34mloading commands... \033[0m");
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
            autoSave = ((Boolean) pluginConfig.get("auto-save", true));
        }
        catch (YAMLException exception) {
            exception.printStackTrace();
        }
    }

    private static boolean loadLanguage() {
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
        Messages.infoString("----------[" + NAME + " " + VERSION +"]----------");
        Messages.infoString("\033[1;33msaving config.yml \033[0m");
        saveConfig();
        Messages.infoString("\033[1;33msaving commands.yml \033[0m");
        saveCommands();
        Messages.infoString("------ all configuration saved ------");

        Messages.infoString("\033[1;36mYou can support us in following websites:  \033[0m");
        Messages.infoString("\033[1;29mGitHub: " + GITHUB + " \033[0m");
        Messages.infoString("\033[1;29mMCBBS: " + MCBBS + " \033[0m");
        Messages.infoString("\033[1;29mJoin the QQ group: \033[1;33m " + QQ_GROUP + " \033[1;29m to get the newest update and some tech-suppositions. \033[0m");

        Messages.infoString("------ Think you for using CustomCommands, see you :) ------");
    }

    private static void setCommandExecutors() {
        plugin.getCommand("customcommandsrun").setExecutor(CCSR_COMMAND_EXECUTOR);
        plugin.getCommand("customcommandsconfig").setExecutor(CCSC_COMMAND_EXECUTOR);
        plugin.getCommand("customcommands").setExecutor(CCS_COMMAND_EXECUTOR);
    }
}
