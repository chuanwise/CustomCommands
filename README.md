# CustomCommands
<p>CustomCommands allows you input some commands with your custom formats.
<p>This is English document. Chinese one will be punished soon.

## Getting Started

## Configuration
### config.yml
The default `config.yml` is:

```yaml
config:       
  # zhcn for Chinese
  lang: zhcn
  debug: false
  max-iterations: 10
```

## commands.yml
The default `commands.yml` is:

```yaml
commands:
  # Permission to execute a command is: ccs.run.<command_name>

  # (required)
  # <command-name>:
  pex-set-group:
    # (required)
    # format: rule to parser command inputted by command sender
    #         You can define some variable in here, like: {variable-name}
    #         a legal variable name should only consist of English alphas, digits and underlines.
    #         the {remain} is a special variable: it's optional, can only appear at the end of
    #         format string when using it.
    #
    format: '{user_id} {group_name}'

    # (optional)
    # usage: a string describing usage of this command.
    #        The usage will be send to command sender when he input wrong command,
    #        If usage doesn't exist, it'll be "/ccsr <command-name> <format-string>" instead.

    # usage: '(unfinished usage string)'

    # (optional)
    # var-nullable: allow or deny null variable
    #               

    # (required)
    # actions: commands will be executed after a user has permission "ccs.run.<command_name>"
    #          input correct command.
    #          [Warning] CustomCommands allow you to input some ccsr command in actions,
    #          Sometime It maybe cause some loop, which may make server shutdown.
    #          So you must design actions carefully when actions include other ccsr command.
    actions:
      - 'pex user {user_name} group set {group_name}'

    # (optional)
    # identify: identify of action commands sender, is "auto" or "console"
    #           "auto" means the action commands sender is the same with ccsr command sender.
    identify: console
```

## commands
All commands start with `/ccs config <remain-arguments>` can be written to `/ccsc <remain-arguments>`.<br>
and 
* `/ccs reload` reload all config for CustomCommands.
* `/ccs config <variable-name>` lookup a variable on `config.yml` and show its value.
* `/ccs config `

