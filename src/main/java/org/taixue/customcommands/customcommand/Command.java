package org.taixue.customcommands.customcommand;

import org.taixue.customcommands.Plugin;
import org.taixue.customcommands.language.Messages;
import org.taixue.customcommands.script.Strings;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

public class Command {
    // 本指令所属的组
    private Group group;

    private String name;
    private String format;
    private String[] parameters;
    private String[] actions;

    private Map<String, String> variableValues = new HashMap<>();
    private Map<String, String> matches = new HashMap<>();

    // 格式错误时返回的内容
    private String usageString;
    // 格式正确时返回内容
    private String resultString;

    private String[] permissions;

    public enum Identify {
        CONSOLE,
        AUTO,
        BYPASS,
        PLAYER
    }
    private String identifyPlayer;

    private Identify identify = Identify.AUTO;

    public Command(String commandName) {
        setName(commandName);
    }

    public Command() {
        setName("");
    }

    /**
     * 解析并将变量值存储
     * @param sender    Command sender
     * @param arguments arguments list, doesn't starts with command name.
     * @return          true only if it's correct arguments.
     */
    public boolean parseCommand(CommandSender sender, String[] arguments) {
        Messages.setVariable("command", name);
        Messages.setVariable("permission", "ccs.run." + group.getName() +"." + name);

        int paraIndex = 0, argsIndex = 1;
        while (paraIndex < parameters.length && argsIndex < arguments.length) {
            String para = parameters[paraIndex ++];
            String arg = arguments[argsIndex ++];

            Messages.setVariable("parameter", para);

            if (para.startsWith("{")) {
                String parameterName = para.substring(1, para.length() - 1);
                variableValues.put(parameterName, arg);
            }
            else if (!para.equals(arg)) {
                return false;
            }
        }

        if (paraIndex < parameters.length) {
            // 形参多余：若多余的是 remain 则将其设置为空，否则语法错误
            if (paraIndex == parameters.length - 1 && parameters[paraIndex].equals("{remain}")) {
                Messages.setVariable("parameter", "remain");
                variableValues.put("remain", "");
                return true;
            }
            else {
                return false;
            }
        }

        if (argsIndex < arguments.length) {
            // 实参多余：若最后一个形参是 remain，那全部都放进去，否则语法错误
            if (parameters[parameters.length - 1].equals("{remain}")) {
                StringBuilder remainValue = new StringBuilder(variableValues.getOrDefault("remain", ""));
                while (argsIndex < arguments.length) {
                    remainValue.append(" ").append(arguments[argsIndex]);
                    argsIndex++;
                }
                variableValues.put("remain", remainValue.toString());
                return true;
            }
            else {
                return false;
            }
        }

        return true;
    }

    public ArrayList<String> getParsedActions() {
        ArrayList<String> actionStrings = new ArrayList<>();

        for (String cmd: actions) {
            actionStrings.add(Messages.replaceVariableString(replaceVariables(cmd)));
        }
        return actionStrings;
    }

    /**
     * 解析输入合法性
     * @param arguments 参数列表
     * @return          true 当且仅当输入合法
     */
    public boolean isLegalArguments(String[] arguments) {
        if (arguments.length == 0 || !arguments[0].equals(group.getName())) {
            return false;
        }

        int argIndex = 1;
        int paraIndex = 0;
        while (argIndex < arguments.length && paraIndex < parameters.length) {
            String argument = arguments[argIndex ++];
            String parameter = parameters[paraIndex ++];

            if (parameter.charAt(0) != '{' && !parameter.equals(argument)) {
                return false;
            }
            if (parameter.charAt(0) == '{') {
                String parameterName = parameter.substring(1, parameter.length() - 1);
                Messages.setVariable("parameter", parameterName);

                if (matches.containsKey(parameterName)) {
                    try {
                        String regex = matches.get(parameterName);
                        Messages.setVariable("regex", regex);
                        if (!argument.matches(regex)) {
                            return false;
                        }
                    } catch (PatternSyntaxException exception) {
                        Messages.setException(exception);
                        Messages.severeLanguage("exceptionInMatchingArgument");
                        exception.printStackTrace();
                        return false;
                    }
                }
            }
        }

        if (argIndex < arguments.length && !parameters[parameters.length - 1].equals("{remain}")) {
            return false;
        }
        if (paraIndex < parameters.length && !parameters[parameters.length - 1].equals("{remain}")) {
            return false;
        }
        return true;
    }

    public boolean isLegalParameters() {
        Set<String> variableNames = new HashSet<>();
        for (int index = 0; index < parameters.length; index++) {
            String parameter = parameters[index];
            if (parameter.charAt(0) == '{') {
                // 检查变量名合法性
                if (parameter.matches("\\{" + Strings.LEGAL_VARIABLE_NAME_REGEX + "\\}")) {
                    String parameterName = parameter.substring(1, parameter.length() - 1);
                    Messages.setVariable("parameter", parameterName);
                    // 变量重定义，或不在末尾使用 remain
                    if (variableNames.contains(parameterName) ||
                            (index != parameters.length - 1 && parameterName.equals("remain"))) {
                        return false;
                    }
                    else {
                        variableNames.add(parameterName);
                    }
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLegalMatches() {
        Set<String> variableNames = new HashSet<>();
        for (String parameter: parameters) {
            if (parameter.charAt(0) == '{') {
                variableNames.add(parameter.substring(1, parameter.length() - 1));
            }
        }
        for (String regexHead: matches.keySet()) {
            Messages.setVariable("parameter", regexHead);
            if (regexHead.equalsIgnoreCase("remain")) {
                return false;
            }
            if (!variableNames.contains(regexHead)) {
                return false;
            }
            try {
                "".matches(matches.get(regexHead));
            }
            catch (PatternSyntaxException exception) {
                Messages.setException(exception);
                Messages.severeLanguage("regexSyntaxError");
                return false;
            }
        }
        return true;
    }

    /**
     * 替换字符串内所有变量名为其对应值
     * @param string    替换前的字符串
     * @return          替换后的字符串
     */
    public String replaceVariables(String string) {
        final int maxInteractions = ((Integer) Plugin.pluginConfig.get("max-iterations"));
        int left, right;

        for (int counter = 0;
             string.contains("{") && counter < maxInteractions;
             counter++) {
            left = string.indexOf("{");
            right = string.indexOf("}", left);

            if (left < right - 1) {
                String variableName = string.substring(left + 1, right);
                String value = variableValues.get(variableName);
                if (Objects.nonNull(value)) {
                    string = string.replaceAll("\\{" + Matcher.quoteReplacement(variableName) + "\\}", value);
                }
            }
        }
        return string;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFormat(String format) {
        this.format = format;
        String[] splittedParameters = format.split("\\s");
        ArrayList<String> parameters = new ArrayList<>();
        for (String parameter: splittedParameters) {
            if (!parameter.isEmpty()) {
                parameters.add(parameter);
            }
        }
        this.parameters = parameters.toArray(new String[0]);
        setUsageToDefault();
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public String[] getActions() {
        return actions;
    }

    public void setUsageString(String usageString) {
        this.usageString = usageString;
    }

    public String getUsageString() {
        return usageString;
    }

    public void setIdentify(Identify identify) {
        this.identify = identify;
    }

    public Identify getIdentify() {
        return identify;
    }

    public String getIdentifyString() {
        if (identify == Identify.PLAYER) {
            return "player:" + getIdentifyPlayer();
        }
        else {
            return identify.toString().toLowerCase();
        }
    }

    public String getIdentifyPlayer() {
        return identifyPlayer;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public String getResultString() {
        return resultString;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Map<String, String> getVariableValues() {
        return variableValues;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public String getFormattedResultString() {
        return replaceVariables(getResultString());
    }

    public void addAction(String actionString) {
        actions = Arrays.copyOf(actions, actions.length + 1);
        actions[actions.length - 1] = actionString;
    }

    public void removeAction(String actionString) {
        List<String> actionList = Arrays.asList(actions);
        actionList.remove(actionString);
        actions = actionList.toArray(new String[0]);
    }

    public void addPermission(String permissionString) {
        permissions = Arrays.copyOf(permissions, permissions.length + 1);
        permissions[permissions.length - 1] = permissionString;
    }

    public void removePermissions(String permissionString) {
        List<String> permissionList = Arrays.asList(permissions);
        permissionList.remove(permissionString);
        permissions = permissionList.toArray(new String[0]);
    }

    public boolean containsAction(String actionString) {
        for (String action: actions) {
            if (action.equals(actionString)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPermission(String permissionString) {
        for (String action: permissions) {
            if (action.equals(permissionString)) {
                return true;
            }
        }
        return false;
    }

    public void setMatches(Map<String, String> matches) {
        this.matches = matches;
    }

    public Map<String, String> getMatches() {
        return matches;
    }

    public void setUsageToDefault() {
        setUsageString("/ccsr " + getGroup().getName() + " " + getFormat());
    }

    public void setIdentifyPlayer(String identifyPlayer) {
        this.identifyPlayer = identifyPlayer;
        identify = Identify.PLAYER;
    }
}
