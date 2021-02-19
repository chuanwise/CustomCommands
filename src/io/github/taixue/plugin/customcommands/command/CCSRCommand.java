package io.github.taixue.plugin.customcommands.command;

import io.github.taixue.plugin.customcommands.Plugin;
import io.github.taixue.plugin.customcommands.path.Path;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * Run the command defined in commands.yml
 */
public class CCSRCommand implements CommandExecutor {
    private MemorySection commands;
    private FileConfiguration fileConfiguration;

    public void loadSavedCommand() {
        if (Plugin.debug) {
            Plugin.logger.info("loading saved command...");
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(new File(Plugin.plugin.getDataFolder(), Path.COMMANDS));
        if (fileConfiguration.contains("commands")) {
            commands = ((MemorySection) fileConfiguration.get("commands"));
        }
        else {
            Plugin.logger.severe("There is no 'commands' on commands.yml!");
            commands = null;
        }
        if (Plugin.debug) {
            Plugin.logger.info("commands: " + commands);
        }
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (Objects.isNull(command)) {
            commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.noAnyLoadedCommand);
            return true;
        }
        if (strings.length >= 1) {
            if (!commandSender.hasPermission("ccs.run." + strings[0])) {
                commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.lackPermission.replaceAll("\\{permission\\}", "ccs.run." + strings[0]));
                Plugin.logger.info(commandSender.getName() + " want to use " + strings[0] + " command, but he don't have enough permissions.");
                return true;
            }
            try {
                if (Plugin.debug) {
                    commandSender.sendMessage(Plugin.language.messageHead + "---------- command: " + strings[0] + " ----------");
                }
                MemorySection customCommand = ((MemorySection) commands.get(strings[0]));

                if (Plugin.debug) {
                    if (Objects.isNull(customCommand)) {
                        commandSender.sendMessage(Plugin.language.messageHead + "cannot found command: " + strings[0]);
                    }
                    else {
                        commandSender.sendMessage(Plugin.language.messageHead + "got command " + strings[0] + " successfully!");
                    }
                }

                if (Objects.nonNull(customCommand)) {
                    String format = ((String) customCommand.get("format"));
                    String[] paras = format.split(" ");
                    if (Plugin.debug) {
                        commandSender.sendMessage(Plugin.language.messageHead + "format: " + format);
                    }

                    String usage = ((String) customCommand.get("usage"));
                    if (Objects.isNull(usage)) {
                        usage = "/ccsr " + strings[0] + " " + format.trim();
                        if (Plugin.debug) {
                            commandSender.sendMessage(Plugin.language.messageHead + "(format string as usage): " + usage);
                        }
                    }
                    else {
                        if (Plugin.debug) {
                            commandSender.sendMessage(Plugin.language.messageHead + "usage: " + usage);
                        }
                    }

                    Map<String, String> argValMap = new HashMap<>();

                    int paraIndex = 0, argsIndex = 1;
                    while (paraIndex < paras.length && argsIndex < strings.length) {
                        String para = paras[paraIndex].trim();
                        String arg = strings[argsIndex].trim();
                        // variable definition
                        if (paras[paraIndex].startsWith("{")) {
                            // valid identify
                            if (para.equals("{remain}") && paraIndex != paras.length - 1) {
                                commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.wrongPositionForRemain);
                                return true;
                            }
                            if (para.matches("\\{\\w+\\}")) {
                                String variableName = para.substring(1, para.length() - 1);
                                if (argValMap.containsKey(variableName)) {
                                    commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.redefinedVariable.replaceAll("\\{variable\\}", variableName));
                                    return true;
                                }
                                else {
                                    argValMap.put(variableName, arg);
                                }
                            }
                            else {
                                commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.wrongFormatForCommandsPara.
                                        replaceAll("\\{command_name\\}", strings[0]).
                                        replaceAll("\\{para_name\\}", para));
                                return true;
                            }
                        }
                        else if (!para.equals(arg)) {
                            commandSender.sendMessage(Plugin.language.messageHead + usage.replaceAll("\\{command_name\\}", strings[0]));
                            return true;
                        }

                        paraIndex++;
                        argsIndex++;
                    }

                    // para are too many
                    if (paraIndex < paras.length) {
                        // set remain variables to null

                        // remain
                        if (paraIndex == paras.length - 1 && paras[paraIndex].equals("{remain}")) {
                            String variableName = "remain";
                            if (argValMap.containsKey(variableName)) {
                                commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.redefinedVariable.replaceAll("\\{variable\\}", variableName));
                                return true;
                            } else {
                                argValMap.put(variableName, "");
                            }
                        }
                        else if (!((Boolean) customCommand.get("var-nullable", false))) {
                            commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.variableCannotBeBull);
                            commandSender.sendMessage(Plugin.language.messageHead + usage.replaceAll("\\{command_name\\}", strings[0]));
                            return true;

                        }
                        else while (paraIndex < paras.length) {
                            String para = paras[paraIndex].trim();
                            if (Plugin.debug) {
                                commandSender.sendMessage(Plugin.language.messageHead + "try to set parameter or string: " + para + " to null string.");
                            }

                            if (para.matches("\\{\\w+\\}")) {
                                String variableName = para.substring(1, para.length() - 1);
                                if (argValMap.containsKey(variableName)) {
                                    commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.redefinedVariable.replaceAll("\\{variable\\}", variableName));
                                    return true;
                                } else {
                                    argValMap.put(variableName, "");
                                }
                            } else {
                                commandSender.sendMessage(Plugin.language.messageHead + usage.replaceAll("\\{command_name\\}", strings[0]));
                                return true;
                            }
                            paraIndex++;
                        }

                    }

                    // args are too many
                    if (argsIndex < strings.length) {
                        if (paras[paras.length - 1].equals("{remain}")) {
                            StringBuilder remain = new StringBuilder(argValMap.getOrDefault("remain", ""));
                            while (argsIndex < strings.length) {
                                remain.append(" ").append(strings[argsIndex]);
                                argsIndex++;
                            }
                            argValMap.put("remain", remain.toString());
                        }
                        else {
                            commandSender.sendMessage(Plugin.language.messageHead + usage.replaceAll("\\{command_name\\}", strings[0]));
                            return true;
                        }
                    }

                    if (Plugin.debug) {
                        commandSender.sendMessage(Plugin.language.messageHead + "variable-value list:");
                        if (argValMap.isEmpty()) {
                            commandSender.sendMessage(Plugin.language.messageHead + "(There is no any parameter)");
                        }
                        else for (String para: argValMap.keySet()) {
                            commandSender.sendMessage(Plugin.language.messageHead + "    > " + para + ":    " + argValMap.get(para));
                        }
                    }

                    List<String> actions = ((List<String>) customCommand.get("actions"));
                    int maxInteractions = ((Integer) Plugin.configuration.get("config.max-iterations"));

                    if (Plugin.debug) {
                        if (actions.isEmpty()) {
                            commandSender.sendMessage(Plugin.language.messageHead + "(no any untranslated action command)");
                        }
                        else {
                            commandSender.sendMessage(Plugin.language.messageHead + "untranslated action command:");
                            for (String cmd: actions) {
                                commandSender.sendMessage(Plugin.language.messageHead + "    > " + cmd);
                            }
                        }
                    }

                    ArrayList<String> actionStrings = new ArrayList<>();
                    // replace the para on action command
                    for (String cmd: actions) {
                        for (int counter = 0;
                             cmd.contains("{") && counter < maxInteractions;
                             counter++) {

                            for (String para: argValMap.keySet()) {
                                cmd = cmd.replaceAll("\\{" + para + "\\}", argValMap.get(para));
                            }
                        }
                        actionStrings.add(cmd);
                    }

                    if (Plugin.debug) {
                        if (actionStrings.isEmpty()) {
                            commandSender.sendMessage(Plugin.language.messageHead + "(no any action command)");
                        }
                        else {
                            commandSender.sendMessage(Plugin.language.messageHead + "action commands:");
                            for (String cmd: actionStrings) {
                                commandSender.sendMessage(Plugin.language.messageHead + "    > " + cmd);
                            }
                        }
                    }

                    // execute it
                    String identify = ((String) customCommand.get("identify", "auto"));

                    if (actionStrings.isEmpty()) {
                        Plugin.logger.info(commandSender.getName() + " will use " + strings[0] + " command with identify " + identify +
                                ", but it haven't any actions to execute.");
                    }
                    else {
                        Plugin.logger.info(commandSender.getName() + " will use " + strings[0] + " command with identify " + identify +
                                ", which was parse to:");
                        for (String cmd: actionStrings) {
                            Plugin.logger.info("    > " + cmd);
                        }
                    }

                    switch (identify) {
                        case "auto":
                            for (String action: actionStrings) {
                                commandSender.getServer().dispatchCommand(commandSender, action);
                            }
                            break;
                        case "console":
                            for (String action: actionStrings) {
                                commandSender.getServer().dispatchCommand(Plugin.plugin.getServer().getConsoleSender(), action);
                            }
                            break;
                        default:
                            commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.wrongIdentify);
                            break;
                    }
                }
                else {
                    commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.undefinedCommand.replaceAll("\\{command_name\\}", strings[0]));
                }
            }
            catch (ClassCastException | NullPointerException exception) {
                commandSender.sendMessage(Plugin.language.messageHead + Plugin.language.wrongFormatForCommandsFile.replaceAll("\\{command_name\\}", strings[0]));
            }
            return true;
        }
        else {
            return false;
        }
    }
}
