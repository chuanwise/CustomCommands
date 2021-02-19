package io.github.taixue.plugin.customcommands.lang;

public class Language {
    public String messageHead = "[Custom-Commands] ";

    public String noAnyLoadedCommand = "There is no any custom commands, you can use /ccs reload to reload configurations.";
    public String wrongFormatForCommandsFile = "Wrong custom command \"{command_name}\": command should be MemorySection";
    public String undefinedCommand = "Undeclared command: \"{command_name}\"";
    public String wrongFormatForCommandsPara = "Wrong parameter name: \"{para_name}\" in command \"{command_name}\". " +
            "parameter name should only consist of alpha, digit and underline.";
    public String extraArgs = "Too many arguments! Consider {remain} instead.";
    public String wrongIdentify = "Wrong identify: identify must be auto or console.";
    public String redefinedVariable = "variable \"{variable}\" redefined";

    public String variableCannotBeBull = "variable can not be null!";
    public String noVariable = "There are no a variable in config.yml called \"{variable}\".";

    public String lackPermission = "You don't have permission {permission}.";

    public String wrongPositionForRemain = "variable {remain} can only appears at the end of format string!";
}
