# CustomCommands：自定义指令
CustomCommands 插件也叫 CCS（CustomCommandS）或 Custom-Commands 等，允许自己设置一些指令的格式，以简化输入。

* 插件 QQ 群：`1028582500`
* 作者：椽子。
* 明城京联合太学，保留所有权利。
* 欢迎<b>在显眼处注明出处</b>的情况下非商用转载。

## 目录
* [基本概念](#基本概念)
  * [指令组](##指令组)
  * [指令分支](##指令分支)
  * [指令查找](##指令查找)
* [演示效果](#演示效果)
  * [设计目标](#设计目标)
  * [实际效果](#实际效果)
    * [错误的指令组名](#错误的指令组名)
    * [指令组名正确但无任何可匹配分支](#指令组名正确但无任何可匹配分支)
    * [匹配某一分支成功](#匹配某一分支成功)
    * [同时能被多个分支匹配](#同时能被多个分支匹配) 
* [前置插件](#前置插件)
* [插件指令](#插件指令)
* [配置文件](#配置文件)
  * [config.yml](#config.yml)
  * [commands.yml](#commands.yml)
* [配置文件示例](#配置文件示例)
  * [config.yml](#config.yml)
  * [commands.yml](#commands.yml)
* [权限节点](#权限节点)
* [画大饼](#画大饼)
  * [已经开发完成的功能](#已经开发完成的功能)
  * [正在开发的功能](#正在开发的功能)
  * [可能要增加的功能](#可能要增加的功能)
* [联系方式](#联系方式)
* [更新日志](#更新日志)
  * [2.0](#2.0)
  * [1.0](#1.0)
* [感谢](#感谢)

## 基本概念
### 指令组
使用 `CCS` 设计的指令都具有指令名。`CCS` 允许多个指令具有相同的名字和不同的参数格式。<br>

例如，你的服务器叫 `Taixue`。你为该服务器设计了一组指令，他们的格式分别是：
* `/ccsc taixue register <password> <email>`
* `/ccsc taixue login <password> <Verification-code>`
* `/ccsc taixue ...`
这些指令都以 `/ccsc taixue` 开头，却具有不同的参数格式，对应不同的功能。<br>

在 `CCS` 中，上述指令同属于<b>指令组</b> `taixue`，它们是 `taixue` 的不同<b>指令分支</b>。<br>

由此可见，指令组的构成单元是<b>指令分支</b>。

### 指令分支
指令分支是指令组的构成单元。每一个指令分支，都对应一种参数格式，和匹配成功后要执行的指令。<br>
在后文有关指令查找的描述中，你会对指令分支有更深刻的理解。

### 指令查找
执行使用 `CCS` 设计的指令的语句一般是：`/ccsr <group-name> <arguments>`。CCS 首先查找名为 `<group-name>` 的指令组，在该指令组下的所有分支中寻找能与当前参数匹配的一种分支。若能确定唯一的匹配当前参数的分支，则执行该指令。

## 演示效果
### 设计目标
如果你希望输入 `/ccsr test something {arg1} {arg2} tail {arg3}` 后自动执行 `say {arg1} {arg2}` 和 `say {arg3}`，同时希望输入 `/ccsr test something {arg1}` 后自动执行 `say {arg1}`，那么 `commands.yml` 应该是：
```yaml
commands:
  # test 指令组下有两个分支：branch1 和 branch2
  test:
    branch1:
      format: 'something {arg1} {arg2} tail {arg3}'
      result: 'a string will be send to command sender after all parsed commands executed.'
      actions:
        - 'say {arg1} {arg2}'
        - 'say {arg3}'
    branch2:
      format: 'something {arg1}'
      actions:
        - 'say {arg1}'
    # 其他分支信息
    # ...
  # 其他指令组信息
  # ...
```
### 实际效果
#### 错误的指令组名
输入了错误的指令组名，将会输出：
```log
>ccsr qwq
[11:22:03] [Server thread/INFO]: [Custom-Commands] 未定义指令组 qwq
```
#### 指令组名正确但无任何可匹配分支
输入正确的指令组名但没有任何分支可以匹配输入格式，将会输出所有该用户可用的分支格式：
```log
>ccsr test
[11:20:13] [Server thread/INFO]: [Custom-Commands] 未在指令组 test 中找到能与当前参数列表匹配的指令
[11:20:13] [Server thread/INFO]: [Custom-Commands] 当前在指令组 test 中加载的指令有：
[11:20:13] [Server thread/INFO]: [Custom-Commands] /ccsr test something {arg1} {arg2} tail {arg3}
[11:20:13] [Server thread/INFO]: [Custom-Commands] /ccsr test something {arg1}
```
#### 匹配某一分支成功
输入匹配第一种分支`/ccsr test something {arg1} {arg2} tail {arg3}`，则自动执行该分支下的指令： `say {arg1} {arg2}` 和 `say {arg3}`，并显示 `a string will be send to command sender after all parsed commands executed.` 信息：
```log
>ccsr test something qwq orz tail qwqw!
[11:24:32] [Server thread/INFO]: [Server] qwq orz
[11:24:32] [Server thread/INFO]: [Server] qwqw!
[11:24:32] [Server thread/INFO]: [Custom-Commands] a string will be send to command sender after all parsed commands executed.
```
打开调试模式后，输出相关解析信息：
```log
>ccsr test something qwq orz tail qwqw!
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >> variable-value list:
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > arg3:    qwqw!
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > arg2:    orz
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > arg1:    qwq
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >> parsed commands:
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > say qwq orz
[11:24:56] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > say qwqw!
[11:24:56] [Server thread/INFO]: [Server] qwq orz
[11:24:56] [Server thread/INFO]: [Server] qwqw!
[11:24:56] [Server thread/INFO]: [Custom-Commands] a string will be send to command sender after all parsed commands executed.
```
可见变量 `{arg1}``{arg2}``{arg3}` 的值符合预期。

输入匹配另一种分支的指令后，可以成功匹配到对应分支：
```log
>ccsr test something qwq
[11:22:31] [Server thread/INFO]: [Custom-Commands] DEBUG >> variable-value list:
[11:22:31] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > arg1:    qwq
[11:22:31] [Server thread/INFO]: [Custom-Commands] DEBUG >> parsed commands:
[11:22:31] [Server thread/INFO]: [Custom-Commands] DEBUG >>     > say qwq
[11:22:31] [Server thread/INFO]: [Server] qwq
[11:22:31] [Server thread/INFO]: [Custom-Commands] 成功执行 test 组中的 branch2 指令
```
#### 同时能被多个分支匹配
如果修改设置，使得多种分支能够同时被匹配，则会输出错误：

配置文件：
```yaml
commands:
  # ...
  test:
    branch1:
      format: 'something {arg1} {arg2} tail {arg3}'
      result: 'a string will be send to command sender after all parsed commands executed.'
      actions:
        - 'say {arg1} {arg2}'
        - 'say {arg3}'
    branch2:
      format: 'something {arg1} {arg2} {arg3} {arg4}'
      actions:
        - 'say {arg1}'
  # ...
```
输入 `/ccsr test something a b tail d` 后的输出：
```log
>ccsr test something a b tail d
[11:42:38] [Server thread/INFO]: [Custom-Commands] 有 2 个指令与之匹配，请修改设置以消除歧义。
[11:42:38] [Server thread/INFO]: [Custom-Commands] /ccsr test something {arg1} {arg2} tail {arg3}
[11:42:38] [Server thread/INFO]: [Custom-Commands] /ccsr test something {arg1} {arg2} {arg3} {arg4}
```
可见此输出符合预期。极大地简化了指令输入。

## 前置插件
暂无。<br>
发布下一个版本之后，可能会需要 `PlaceholderAPI`。

## 插件指令
所有格式是 `/ccs <config | run> <remain-arguments>` 的指令都可以被简化为 `/ccs<c|r> <remain-arguments>`，例如 `/ccs config` 可以简化为 `/ccsc`。`CCSC` 也就是 `CustomCommandS Config`， `CCSR` 是 `CustomCommandS Run`。

下表中的指令对应的权限见[权限节点](#权限节点)。

|指令|作用
|---|---
|`/ccs reload`|重载所有设置
|`/ccs version`|显示插件名、版本号等信息
|`/ccs debug`|开关调试模式
|`/ccsc list`|显示所有已加载的指令组
|`/ccsc add <group-name>`|新增加一个组
|`/ccsc group <group-name>`|查看有关指令组`<group-name>`的信息
|`/ccsc remove <group-name>`|删除指令组`<group-name>`
|`/ccsc group <group-name> add <command-name>`|在指令组中添加分支`<command-name>`
|`/ccsc group <group-name> remove <command-name>`|删除整个指令组`<command-name>`
|`/ccsc group <group-name> rename <new-name>`|重命名指令组为`<new-name>`。该组分支下的所有 `usage` 都会被重置
|`/ccsc group <group-name> command <command-name>`|查看分支`<command-name>`的信息
|`/ccsc group <group-name> command <command-name> rename <new-name>`|重命名分支`<command-name>`为`<new-name>`
|`/ccsc group <group-name> command <command-name> identify <identify>`|设置执行 `actions` 中的指令的身份为 `<identify>`
|`/ccsc group <group-name> command <command-name> format <format>`|设置该分支的解析格式为 `<format>`，可以包含空格或为空。其 `usage` 会被自动重置
|`/ccsc group <group-name> command <command-name> actions add <command>`|在该分支的 `actions` 结尾追加新的指令 `<command>`，可以包含空格或为空
|`/ccsc group <group-name> command <command-name> actions clear`|清空 `actions`
|`/ccsc group <group-name> command <command-name> actions remove <command>`|删除 `actions` 中的第一个出现的指令 `<command>`
|`/ccsc group <group-name> command <command-name> actions set <command>`|设置 `actions` 为指令 `<command>`
|`/ccsc group <group-name> command <command-name> actions edit <index> <command>`|修改 `actions` 中的第 `<index>` 条指令为 `<command>`（`index` 从 `1` 开始）
|`/ccsc group <group-name> command <command-name> result <string>`|修改返回信息 `result` 为 `<string>`，可以包含空格或为空
|`/ccsc group <group-name> command <command-name> usage <usage>`|修改 `usage` 为 `<usage>`，可以包含空格或为空
|`/ccsc group <group-name> command <command-name> permissions add <permission>`|在该分支的必须权限中增加新的权限节点 `<permission>`
|`/ccsc group <group-name> command <command-name> permissions clear`|清除该分支的必须权限。
|`/ccsc group <group-name> command <command-name> permissions remove <permission>`|删除该分支的必须权限中的权限节点 `<permission>`
|`/ccsc group <group-name> command <command-name> permissions set <permission>`|设置必须权限为一条 `<permission>`
|`/ccsc group <group-name> command <command-name> permissions edit <index> <command>`|修改必须权限中的第 `<index>` 个节点为 `<command>`（`index` 从 `1` 开始）
|`/ccsc group <group-name> command <command-name> permissions default`|将必须权限设置为默认值，即一条 `ccs.run.<group-name>.<command-name>`
|`/ccsc val <variable-name>`|查找 `config.yml` 中的设置项 `<variable-name>`，并显示其值
|`/ccsc val <variable-name> <set-to>`|设置 `config.yml` 中的设置项 `<variable-name>` 的值为 `<set-to>`
|`/ccsr <command-name> [arguments-list]`|执行在 `commands.yml` 内定义的指令 `<command-name> `

### 注意
1. 若修改指令组名 / 分支名 / 分支的 format，将会自动修改该组所有分支 / 该分支下的所有 `usage`。

## 配置文件
### config.yml
默认的 `config.yml` 的内容是：

```yaml
config:
  # 语言选项。zhcn 是中文
  lang: zhcn
  # 是否开启调试模式（会输出一些解析信息）
  debug: true
  # 最大迭代次数
  max-iterations: 10
  # 是否自动保存
  auto-save: true
```
#### max-iterations（最大迭代次数）
将变量替换为其值的最大次数<br>
对于变量 `{remain}`，如果它的内容是 `head {remain}`，在下面这个例子中<br>
`最后执行的指令格式`: `/say {remain}`, 输入格式：`/.. head {remain}`<br>
`解析结果`: `/say head head head ... head {remain}`<br>
显然，变量 `max-iterations` 控制了变量的值中仍存在变量的情况下，最大的迭代次数。

#### auto-save（自动保存）
默认值：`true`

使用 `/ccsc` 开头的配置指令后是否立即写入文件保存。<br>
若选择 `false`，插件将在关闭服务器时自动保存所做的修改。你也可以使用 `/ccsc save` 手动保存。

### commands.yml
默认的 `commands.yml` 的内容是：

```yaml
commands:
  # 执行一个自定义指令组的权限默认是：ccs.run.<指令组名>.<分支名>

  # （必填）
  # <指令组名>:
  group-name:
    # （必填）
    # 分支名
    branch-name:
      # （必填）
      format: '{user_name} {group_name}'

      # （必填）
      actions:
      - 'pex user {user_name} group set {group_name}'
      - 'broadcast {user_name} is a member of {group_name}.'

      # identify: 'console'

      # usage: '格式错误时显示给指令发送者的内容'

      # result: '成功执行后显示给指令发送者的内容'

      # 一旦修改 permissions 将不会自动验证用户是否具有权限 ccs.run.<指令组名>.<分支名>，除非在 permissions 中添加该项。
      # permissions:
      #   - ccs.run.<指令组名>.<分支名>
      #   - 其他执行此分支的指令必备的权限节点
```
### format（解析规则）
解析输入的指令的规则。
由变量定义和普通字符串组成。

你可以在 format 中定义变量，就像 `{variable_name}`。<br>
一个合乎规则的变量名必须<b>仅由</b>英文字母、数字和下划线组成，并且不以数字开头。

format 中还可以存在普通字符串，以便于区分指令分支。

变量 `{remain}` 是一个很特殊的变量。如果在 `format` 中使用，它必须出现在末尾。<br>
在解析的时候，如果遇到 `{remain}` ，指令剩余的所有内容都会存入这个变量中，所以它的内容可能包含空格，也可能为空。

### actions（执行指令）
在输入了正确的 `/ccsr` 指令后将会执行的命令<br>
如果这当中出现了没有在 `format` 中定义的变量，它们会保持原样。

<b>警告</b>：

 CCS 允许你在 actions 中写入 `/ccsr` 指令，这可能会导致循环。例如下面的情况：

```yaml
commands:
  # ...
  death-loop:
    # 分支名可以随便起。
    this:
      format: '{remain}'
      actions:
        - 'ccsr death-loop {remain}'
```
这会导致服务器后台输出大量错误信息随后崩服。<br>
为了保护你的服务器，在设计 actions 时，<b>必须</b> 在添加额外的 `/ccsr` 时仔细考虑。

### usage（选填）
默认值：`/ccsr <指令组名> <分支名> <解析规则>`。

在上面的 `pex-group-set` 命令中，`usage` 的值是 `/ccsr pex-group-set {user_name} {group_name}`

这是描述该指令用法的字符串，他将在使用指令的人输入了一种无法被当前指令组中任何一种指令分支匹配，或能同时被多个分支匹配时显示。<br>

### identify（选填）
默认值： `auto`。<br>
执行 `actions` 时的身份，可以是 `auto` 也可以是 `console`。`auto` 就是以当前输入 `/ccsr` 指令的身份执行 `actions`，`console` 就是以控制台身份执行 `actions`。<p>
这个设置项允许你仅使用 `console` 时可以通过权限 `ccs.run.<指令名>` 跳过这一串指令的权限检测。当然，在未来这个插件还可能支持 `bypass` 模式（以玩家身份无视权限执行）。

### permissions（选填）
默认值： `[ccs.run.<group-name>.<branch-name>]`。

匹配当前指令组所需的权限。

如果你希望拥有不同权限的玩家在输入相同的指令格式时当做不同的指令分支解析，使用 `permissions` 加以区分是一种很好的办法。当检测到当前指令格式可以匹配多种指令分支，`CCS` 会首先尝试筛除玩家不具有权限的那些分支，如果筛除后仅剩下一种分支，`CCS` 会执行该指令。

### result（选填）
默认值：`成功执行 <group-name> 组中的 <branch-name> 指令`。

指令匹配成功并执行结束后，发送给指令发送者的信息。

## 配置文件示例
### config.yml
```yaml
config:       
  # zhcn 是中文， en 是英文
  lang: zhcn

  # 开关 debug 模式
  debug: false

  # 最大迭代次数
  max-iterations: 10
```
### commands.yml
```yaml
commands:
  # 有关权限设置的所有指令
  pex:
    # set 分支
    set:
      # 匹配 set 分支的格式
      format: 'set {group_name} {user_name}'
      # 匹配成功后执行的指令
      actions:
        - 'pex user {user_name} group set {group_name}'
        - 'bc {user_name} is a member of {group_name}.'
      identify: console

    # 给用户在某一世界中的建筑权限分支
    give-world-builder:
      format: 'give {user_name} {world_name}'
      actions:
        - 'pex user {user_name} add multiverse.access.{world_name}'
        - 'pex user {user_name} group add p-world-builder {world_name}'
        - 'pex reload'
      result: '成功给了 {user_name} 在世界 {world_name} 中的建造权限。'
      identify: console

    # 移除用户在某一世界中的建筑权限分支
    remove-world-builder:
      format: 'remove {user_name} {world_name}'
      actions:
        - 'pex user {user_name} remove multiverse.access.{world_name}'
        - 'pex user {user_name} group remove p-world-builder {world_name}'
        - 'pex reload'
      result: '成功移除 {user_name} 在世界 {world_name} 中的建造权限。'
      identify: console

  # 和经济相关的指令组
  eco:
    give:
      format: '{user_name} {money_number}'
      actions:
        - 'eco give {user_name} {money_number}'
    # ...
```
按以上设置后，便可通过指令 `/ccsr pex set <group-name> <user-name>` 匹配 `pex.set` 分支，用 `/ccsr pex give <user-name> <world-name>` 匹配 `pex.give` 分支，并执行对应分支内的指令。

## 权限节点
|权限节点|作用
|---|---
|`ccs.*`|所有 CCS 权限。
|`ccs.version`|查看插件名、版本等信息
|`ccs.config.debug`|开关调试模式
|`ccs.config.reload`|重载 CCS 插件
|`ccs.config.list`|查看所有已加载的指令组
|`ccs.config.add`|添加一个指令组
|`ccs.config.remove`|删除一个指令组
|`ccs.config.group`|配置一个指令组的设置
|`ccs.run.<指令组名>.<指令分支名>`|使用指令组 `指令组名` 中的 `指令分支名` 分支。
|`ccs.config.val`|查看和设置一个项目

### 注意
1. `ccs.run.<指令组名>.<指令分支名>` 是默认的执行某一分支的权限。若修改该分支的 `permissions`，将以具体的 `permissions` 为准。

## 画大饼
欢迎提交 issue 反馈你希望增加的功能。

### 已经开发完成的功能
下面枚举的功能将在下一个版本更新时发布。
暂无

### 正在开发的功能
1. 使用正则表达式检查变量名。

### 可能要增加的功能
下面枚举的功能我有相关想法，但是不知道实际使用性如何，需要大家的反馈才会决定是否要开发。
1. 允许直接使用 `/<指令组名> <分支名> [参数列表]` 的形式执行指令，无需使用 `/ccsr`

## 联系方式
* QQ group（QQ 群）:  `1028582500`
* Author（作者）: Chuanwise
* E-mail（邮箱）: chuanwise@qq.com
* 明城京联合太学，保留所有权利。
* Taixue, All rights reserved.

## 更新日志
### 3.0
发布于 `2021年2月25日`
1. 进一步完善了消息提示系统和 `zhcn.json` 汉化提示包。
1. 完善了大部分指令，支持使用指令编辑 `commands.yml`。
1. 允许在 `actions` 中使用 `@` 开头的特殊指令。
1. 增加了默认变量：`{player}`, `{displayName}`, `{world}`, `{group}` 和 `{UUID}`。

### 2.0
发布于 `2021年2月22日`
1. 完善了消息提示系统，使用了一些颜色让插件输出更加赏心悦目。
1. 完成了 `zhcn.json` 汉化提示包。
1. 从原本的单一指令系统，升级为指令分组，按格式匹配的系统。增加了自定义指令的自由度。
1. 优化了之前代码中的一些不合理的部分。

### 1.0
发布于 `2021年2月19日`。

## 其他发布地址
* [MCBBS](https://www.mcbbs.net/thread-1172706-1-1.html)

## 感谢
* `Favourite`：引导我入门了最基础的 `Minecraft` 服务器技术。
* `Eric`：[@ExerciseBook](https://github.com/ExerciseBook)
* `One47`
* `Coloryr`