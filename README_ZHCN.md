# CustomCommands
<p>CustomCommandS 插件也叫 CCS，允许自己设置一些指令的格式，以简化输入。
<p>This is Chinese Document, click to see [English Document](https://github.com/Chuanwise/CustomCommands/tree/main/README_EN.md)

* 插件 QQ 群：`1028582500`
* 作者：椽子。
* 明城京联合太学，保留所有权利。

## 前置插件
暂无。<br>
发布下一个版本之后，可能会需要 `PlaceholderAPI`。

## Commands
所有格式是 `/ccs <config | run> <remain-arguments>` 的指令都可以被简化为 `/ccs<c | r> <remain-arguments>`.，例如 `/ccs config` 可以简化为 `/ccsc`。<br>
`CCSC` 也就是 `CustomCommandS Config`， `CCSR` 是 `CustomCommandS Run`

* `/ccs reload`                     重载所有设置
* `/ccs version`                  显示插件名、版本号等信息
* `/ccs debug`                    开关调试模式

* `/ccsc <variable-name>`                 查找 `config.yml` 中的设置项 `<variable-name>`，并显示其值。
* `/ccsc <variable-name> <set-to>`        设置 `config.yml` 中的设置项 `<variable-name>` 的值为 `<set-to>`。

* `/ccsr <command-name> [arguments-list]` 执行在 `commands.yml` 内定义的指令 `<command-name> `。

## 配置文件
### config.yml
默认的 `config.yml` 的内容是：

```yaml
config:       
  # zhcn 是中文， en 就是英文
  # 语言系统还没写完 _(:з」∠)_，所以修改这个值并不会造成什么影响。在未来的版本中这个设置项可能会发挥作用。
  lang: zhcn

  # 开关 debug 模式
  debug: false

  # 最大迭代次数
  max-iterations: 10
```
#### max-iterations（最大迭代次数）
将变量替换为其值的最大次数<br>
对于变量 `{remain}`，如果它的内容是 `head {remain}`，在下面这个例子中<br>
`最后执行的指令格式`: `/say {remain}`, 输入格式：`/.. head {remain}`<br>
`解析结果`: `/say head head head ... head {remain}`<br>
显然，变量 `max-iterations` 控制了变量的值中仍存在变量的情况下，最大的迭代次数。

### commands.yml
默认的 `commands.yml` 的内容是：

```yaml
commands:
  # 执行一个自定义指令的权限是：ccs.run.<command_name>

  # （必填）
  # <指令名>:
  # 请不要以 - 开头。1.0 版本中以 - 开头并不会产生错误，但是在高版本的 CCS 中这可能导致严重错误。
  pex-group-set:

    # （必填）
    format: '{user_name} {group_name}'

    # （必填）
    actions:
      - 'pex user {user_name} group set {group_name}'
      - 'broadcast {user_name} is a member of {group_name}.'

    # （选填）
    identify: console

    # （选填）
    # usage: '（一个描述该指令用途的字符串）'

    # （选填）
    # var-nullable: true

    # （选填）
    # identify: console
```
### format（解析规则）
解析输入的指令的规则。你可以在 format 中定义一些变量，就像 `{variable_name}`。<br>
一个合乎规则的变量名必须仅由英文字母、数字和下划线组成，并且不以数字开头。<br>

<p>`1.0`版本下使用数字开头的变量名并不会产生错误，但可能在高版本中出现不可预料的问题。

变量 `{remain}` 是一个很特殊的变量。如果在 `format` 中使用，它必须出现在末尾。<br>
在解析的时候，如果遇到 `{remain}` ，指令剩余的所有内容都会存入这个变量中，所以它的内容可能包含空格。

### actions（执行指令）
在输入了正确的 `/ccsr` 指令后将会执行的命令<br>
如果这当中出现了没有在 `format` 中定义的变量，它们会保持原样。

<p><b>警告</b>： CCS 允许你在 actions 中写入 `/ccsr` 指令，这可能会导致循环。例如下面的情况：

```yaml
commands:
  # ...
  death-loop:
    format: '{remain}'
    actions:
      - 'ccsr death-loop {remain}'
```
这会导致服务器后台输出大量错误信息随后崩服。<br>
为了保护你的服务器，在设计 actions 时，<b>必须</b> 在添加 `/ccsr` 时仔细考虑。

### usage （选填）
默认值：`/ccsr <指令名> <解析规则>`.<br>
<p>在上面的 `pex-group-set` 命令中，`usage` 的值是 '/ccsr pex-group-set {user_name} {group_name}'

<p>这是描述该指令用法的字符串<br>
他将在使用指令的人输入了错误的格式时显示<br>

### var-nullable （选填）
默认值：`false`.<br>
<p>如果这个值设置为 `true`，可能会导致对同一个解析规则，存在多个合法输入模式。例如：<br>
`解析规则`: `{arg1} {arg2} {arg3}`<br>
`合法输入`: `/ccsr <command-name>`<br>
         | `/ccsr <command-name> <arg1>`<br>
         | `/ccsr <command-name> <arg1> <arg2>`<br>
         | `/ccsr <command-name> <arg1> <arg2> <arg3>`<br>
并且有的变量可能为空值。

<p><b>提示</b>：变量 `{remain}` 永远可以为空值，并不受此规则约束。

### identify （选填）
默认值： `auto`.<br>
<p>执行 `actions` 时的身份，可以是 `auto` 也可以是 `console`。`auto` 就是以当前输入 `/ccsr` 指令的身份执行 `actions`，`console` 就是以控制台身份执行 `actions`。<p>
这个设置项允许你仅使用 `console` 时可以通过权限 `ccs.run.<指令名>` 跳过这一串指令的权限检测。当然，在未来这个插件还可能支持 `bypass` 模式（以玩家身份无视权限执行）。

## 权限节点
* `ccs.*`: 所有 CCS 权限。
* `ccs.version`: 查看插件名、版本等信息的权限。
* `ccs.debug`: 开关调试模式的权限。
* `ccs.reload`: 重载 CCS 插件的权限。
* `ccs.run.<指令名>`: 执行指令 `指令名` 的权限，例如上面的例子是 `ccs.run.pex-group-set`。
* `ccs.config.val.set`: 设置一个项目的权限
* `ccs.config.val.look`: 查看一个项目的值的权限