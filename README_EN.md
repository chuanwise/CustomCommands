# CustomCommands
<p>CustomCommands allows you input some commands with your custom formats.
<p>This is Chinese Document, click to see [EnglishDocument](https://github.com/Chuanwise/CustomCommands/tree/main/README_EN.md)

* QQ group: `1028582500`
* Author: Chuanwise
* Taixue, All rights reserved.

## Getting Started
A Spigot or Bukkit Minecraft server.

## Front Plugins
It's empty.<br>
PlaceholderAPI will be required after publish the next version.

## Commands
All commands start with `/ccs <config | run> <remain-arguments>` can be written to `/ccs<c | r> <remain-arguments>`.<br>
`CCSC` means `CustomCommandS Config`, and `CCSR` means `CustomCommandS Run`

* `/ccs reload`                   reload all config for CustomCommands.
* `/ccs version`                  show version of CustomCommands and some other message of this plugin.
* `/ccs debug`                    enable or disable the debug mode.

* `/ccsc <variable-name>`                 lookup variable `<variable-name>` in `config.yml` and show its value.
* `/ccsc <variable-name> <set-to>`        set variable `<variable-name>` to `<set-to>`.

* `/ccsr <command-name> [arguments-list]` execute command `<command-name> ` designed in `commands.yml`.

## Configuration
### config.yml
The default `config.yml` is:

```yaml
config:       
  # zhcn for Chinese, en for English
  # Language system has not been finished yet _(:з」∠)_, so it'll cause nothing if you change this value.
  lang: zhcn

  # enable or disable debug mode.
  debug: false
  max-iterations: 10
```
#### max-iterations
Replacing valuable names to its actual value string can be recursive.<br>
such as variable `{remain}`, if its value = `head {remain}` in the following situation:<br>
`action command format`: `/say {remain}`, input: `/.. head {remain}`<br>
`result`: `/say head head head ... head {remain}`<br>
the `max-iterations` control the maximum times in iterations.

### commands.yml
The default `commands.yml` is:

```yaml
commands:
  # Permission to execute a command is: ccs.run.<command_name>

  # (required)
  # <command-name>:
  pex-group-set:

    # (required)
    format: '{user_name} {group_name}'

    # (required)
    actions:
      - 'pex user {user_name} group set {group_name}'
      - 'broadcast {user_name} is a member of {group_name}.'

    # (optional)
    # identify: identify of action commands sender, is "auto" or "console".
    #           "auto" means the action commands sender is the same with ccsr command sender.
    identify: console

    # (optional)
    # usage: '(a usage string)'

    # (optional)
    # var-nullable: true

    # (optional)
    # identify: console
```
### format
Grammar to parser command inputted by command sender. You can define some variable in here, like: `{variable_name}`.<br>
A legal variable name should only consist of English alphas, digits and underlines, and it cannot starts with digit.<br>

The `{remain}` is a special variable: it's optional, can only appear at the end of format string when using it.<br>
When parsing, all remain part of commands will be set to it, so the value of `{remain}` can contains spaces.

### actions
commands will be executed after input correct command.<br>
If there are some undeclared variable in action commands, it won't be replaced to a value.

<p><b>WARNING</b><br> CustomCommands allow you to input some ccsr command in actions, Sometime It maybe cause loops, which may make server show a lot of Exception message and shutdown. So in order to protect your server, you <b>must</b> design action commands carefully when they include a `/ccsr` command.

### usage (optional)
default value: `/ccsr <command-name> <format-string>`.<br>
<p>default value in above case is '/ccsr pex-group-set {user_name} {group_name}'

<p>A string describing usage of this command.<br>
The usage will be send to command sender when he input a wrong command.<br>

### var-nullable (optional)
default value: `false`.<br>
<p>If it set to `true`, there are many legal input format for same parsing format:<br>
`parsing format`: {arg1} {arg2} {arg3}<br>
`legal input formats`: `/ccsr <command-name>`<br>
                     | `/ccsr <command-name> <arg1>`<br>
                     | `/ccsr <command-name> <arg1> <arg2>`<br>
                     | `/ccsr <command-name> <arg1> <arg2> <arg3>`<br>
and some variable will be set to null in this case.

<p><b>NOTICE</b><br>variable `{remain}` doesn't restricted.

### identify (optional)
default value: `auto`.<br>
<p>identify of action commands sender, is `auto` or `console`. `auto` means the action commands sender is the same with ccsr command sender.


## Permissions
* `ccs.*`: Give players with op everything by default.
* `ccs.version`: Permission to see version, name and other information of CustomCommands.
* `ccs.debug`: Permission to enable or disable debug mode.
* `ccs.reload`: Permission to reload all configurations.
* `ccs.run.<command-name>`: Permission to execute a custom command.
* `ccs.config.val.set`: set value of variable in config.yml
* `ccs.config.val.look`: look value of variable in config.yml