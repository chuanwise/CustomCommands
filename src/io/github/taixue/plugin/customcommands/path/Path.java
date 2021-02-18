package io.github.taixue.plugin.customcommands.path;

public class Path {
    private Path() {}

    public static final String SPLIT = "/";

    public static final String RESOURCE_DIR = "resources";

    public static final String LANG_DIR = RESOURCE_DIR + SPLIT + "lang";

    public static String LANG = LANG_DIR + SPLIT + "zhcn.yml";

    public static final String CONFIG = "config.yml";
    public static final String COMMANDS = "commands.yml";

    public static final String DEFAULT_CONFIG = RESOURCE_DIR + SPLIT + CONFIG;
    public static final String DEFAULT_COMMANDS = RESOURCE_DIR + SPLIT + COMMANDS;

    public static final String STORED_CONFIG = "plugins/CustomCommands/" + CONFIG;
    public static final String STORED_COMMANDS = "plugins/CustomCommands/" + COMMANDS;
}
