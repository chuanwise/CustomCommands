# CustomCommands：自定义指令
`CustomCommands` 插件也叫 `CCS（CustomCommandS）`或 `Custom-Commands` 等，允许自己设置一些指令的格式，以简化输入。

* 插件 QQ 群：`1028582500`
* 作者：椽子。
* 明城京联合太学，保留所有权利。
* 请遵循 `GNU` 开源协议使用 `CCS`。

## 目录
* [CCS能做什么？](#CCS能做什么？)
  * [给指令打包](##给指令打包)
  * [更改指令的参数顺序](##更改指令的参数顺序)
* [基本概念](#基本概念)
  * [指令组](#指令组)
  * [指令分支](#指令分支)
    * [参数列表](#指令查找)
    * [执行指令](#指令查找)
  * [变量](#变量)
* [前置插件](#前置插件)
* [配置文件](#配置文件)
  * [config.yml](#config.yml)
  * [commands.yml](#commands.yml)
* [插件指令](#插件指令)
* [权限节点](#权限节点)
* [画大饼](#画大饼)
  * [已经开发完成的功能](#已经开发完成的功能)
  * [正在开发的功能](#正在开发的功能)
  * [可能要增加的功能](#可能要增加的功能)
* [高级功能](#高级功能)
  * [变量表机制](#变量表机制)
  * [模式匹配机制](#模式匹配机制)
* [联系方式](#联系方式)
* [更新日志](#更新日志)
  * [4.0](#4.0)
  * [3.0](#3.0)
  * [2.0](#2.0)
  * [1.0](#1.0)
* [其他发布地址](#其他发布地址)
* [感谢](#感谢)

## CCS能做什么？
### 给指令打包
在使用 `PermissionEX` （一款老牌的权限组插件）的服务器上，输入相关指令是件令人头疼的事情。

例如，你也许需要输入下面的指令，给予玩家在某个世界内的权限，并全服广播：
* `/pex user <玩家ID> add <权限节点> <世界名>`
* `/pex reload`
* `/bc 恭喜玩家 <玩家ID> 获得了在世界 <世界名> 中的权限 <权限节点> ！`

`CCS` 让你可以只给出必要的 `<玩家ID>` `<权限节点>`和`<世界名>`这三个关键信息，就自动执行上面这一串指令。这就把三个指令打包为一个指令。

### 更改指令的参数顺序
如果你不喜欢下面这个指令的参数顺序
* `/pex user <玩家ID> add <权限节点> <世界名>`

完全可以将其打包为另一个指令，至于在哪个位置输入`<玩家ID>`这些信息，完全你设计！

## 基本概念
### 指令组
如果你设计一组指令（执行自定义指令都需要加相同的 `/ccsr` 前缀）：
|格式|作用
|---|---
|`/ccsr story tell <玩家>`|给玩家讲故事
|`/ccsr story stop`|停止给玩家讲故事
|`/ccsr taixue register <password>`|注册在太学服务器的账号

前两条指令都以 `/ccsr story` 开头，都是和故事相关的操作。这两个指令便组成了一个<b>指令组</b>`story`。这两种参数形式，对应不同功能，分别是这个组的不同<b>指令分支</b>。

第三条指令以 `/ccsr taixue` 开头，所以属于<b>指令组</b>`taixue`。

### 指令分支
指令分支是在同一个指令组中，对应不同功能和参数列表的子指令。例如上面的前两条指令对应指令组 `story` 的不同指令分支。

每一个指令分支都至少需要一个确定的<b>参数列表</b>和<b>执行指令</b>

#### 参数列表
除去相同的`/ccsr <指令组名>`，剩余的内容叫这个指令分支的<b>参数列表</b>。例如：
|原指令|参数列表
|---|---
|`/ccsr story tell <玩家>`|`tell <玩家>`
|`/ccsr story stop`|`stop`

对于上表第一个分支，`/ccsr story tell chuanwise` 是一个正确的输入，此时`<玩家>`就是`chuanwise`。

`/ccsr story tell chuanwise zzz`是一个错误的输入，因为 `story` 组内没有任何一个分支是这个格式。

#### 执行指令
执行指令是一个指令分支中，输入正确的参数后执行的一串指令。

例如，如果你希望玩家输入 `/ccsr story tell <玩家>` 后执行下面一串指令：
1. `/tell <玩家> 从前有座山`
1. `/tell <玩家> 山上有座庙`
1. `/tell <玩家> 庙里有个Chuanwise在讲故事`

这一串指令就是这个分支的<b>执行指令</b>。

### 变量
变量是一个值的代号。例如上文的 `玩家` 就是一个变量，它的值可能是 `chuanwise`，也可能是其他任何值。

变量可以在执行指令中使用，可以在玩家输入的参数中读入或自行设定。

## 前置插件
暂无 \_(:з」∠)\_。

## 配置文件
### config.yml
默认的 `config.yml` 的内容是：

```yaml
config:
  # 语言选项。zhcn 是中文
  lang: zhcn

  # 是否开启调试模式（会输出一些解析信息）
  # debug: true

  # 最大迭代次数
  # max-iterations: 10

  # 修改配置后是否自动保存
  # auto-save: true

  # 玩家退出服务器多久（毫秒）卸载其私人变量表
  # wait-for-unload: 300000
```
#### max-iterations（最大迭代次数）
将变量替换为其值的最大次数。

对变量 `{remain}`，如果它的内容是 `head {remain}`，不论怎么展开，最终的值都是 `head head head ... {remain}`。
变量 `max-iterations` 控制了变量的值中仍存在变量的情况下，最大的迭代次数，也就是上例中 `head` 的数量。

#### auto-save（自动保存）
默认值：`true`

修改插件配置后是否立即保存。若选择 `false`，插件将在关闭服务器时自动保存所做的修改。你也可以使用 `/ccsc save` 手动保存。

### commands.yml
默认的 `commands.yml` 的内容是：

```yaml
commands:
  # 执行一个自定义指令组的权限默认是：ccs.run.<指令组名>.<分支名>

  # 指令组名
  group-name:

    # 分支名
    branch-name:

      # 该分支的参数列表
      format: 'set {user_name} {group_name}'

      # 该分支的执行指令
      actions:
      - 'pex user {user_name} group set {group_name}'
      - 'broadcast {user_name} is a member of {group_name}.'

      # 执行执行指令时的身份
      # identify: console

      # usage: '格式错误时显示给指令发送者的内容'

      # result: '成功执行后显示给指令发送者的内容'
```

#### format（参数列表）
解析输入的指令的规则，由变量定义和普通文字组成。

使用 `{}` 包围的内容是变量定义。括号内是该变量的名字。一个合规的变量名<b>必须仅由</b>英文字母、数字和下划线组成，且不以数字开头。如 `{favouritesc}`、`{chuanwise}` 和 `{favouritesc_chuanwise}` 都是合规的变量名。

变量 `{remain}` 是一个很特殊的变量。如果在 `format` 中定义，必须出现在末尾。<br>
解析时候，如遇 `{remain}` ，指令剩余的所有内容都会存入这个变量中。它的内容可能包含空格，也可能为空。

例如对 `/ccsr player {name} {remain}`，对不同的输入：
|输入|`{name}` 的值|`{remain}` 的值
|---|---|---
|`/ccsr player chuanwise`|`chuanwise`|（空）
|`/ccsr player chuanwise qwq`|`chuanwise`|`qwq`
|`/ccsr player chuanwise qwq orz`|`chuanwise`|`qwq orz`

#### actions（执行指令）
输入格式正确后将会执行的一串命令。

`actions` 的每一个语句中都可以使用变量，格式为 `{变量名}`，它们会被替换为该变量的值。

如果出现了无法找到的变量，它们会保持原样。有关变量的查找方式，请查阅高级内容[变量表机制](#变量表机制)。

##### 预定义变量表
下面的变量无须在 `format` 中定义便可以使用。如果在 `format` 中定义了同名的变量，则以 `format` 读入的为准。
|变量名|值
|---|---
|`{player}`|玩家名。如果是控制台身份，则为 `CONSOLE`
|`{displayName}`|玩家显示出的名字，包含前后缀等信息
|`{world}`|玩家所在世界名。控制台身份无此变量
|`{group}`|当前指令分支所处的组。控制台身份无此变量
|`{UUID}`|玩家的 UUID。控制台身份无此变量

##### 脚本
`actions` 中以 `@` 开头的都是脚本，如下表所示：
|脚本名|格式|参数解释|作用
|---|---|---|---
|`sleep`|`@sleep <时长>`|停顿秒数，单位<b>毫秒</b>|在此处停顿若干毫秒后继续执行后面的 `actions` 语句
|`message`|`@message <信息>`|一段信息，可以包含空格|将这段信息发送给指令发送者
|`title`|`@title <主标题> [副标题]`|一个主标题和副标题，副标题可以包含空格|为玩家显示一个标题

##### 注意
1. 自循环<br>
    `CCS` 允许你在 actions 中写入 `/ccsr` 指令，这可能会导致循环。例如下面的情况：
    ```yaml
    commands:
      # ...
      death-loop:
        # 分支名可以随便起
        this:
          format: '{remain}'
          actions:
            - 'ccsr death-loop {remain}'
    ```
    这会导致服务器后台输出大量错误信息随后崩服。<br>
    为了保护你的服务器，在设计 actions 时，强烈建议在添加额外的 `/ccsr` 时仔细考虑。
1. `@sleep 时长`：请确保停顿时长小于 `spigot.yml` 中的 `settings.timeout-time`，否则服务器会被判定为卡死，随后崩服。
例如，当 `settings.timeout-time` 为 `60` 时，意味着服务器无响应 `60` 秒会被判定为卡死。为确保不会崩服，`@sleep` 的参数应该小于 `50000`。
1. `@title` 无法被应用在控制台上。

#### usage（选填）
默认值：`/ccsr {group} {format}`。

在上面的例子中，`usage` 的值默认是 `/ccsr group-name set {user_name} {group_name}`

这是描述该指令用法的字符串，他将在使用指令的人输入了一种无法被当前指令组中任何一种指令分支匹配，或能同时被多个分支匹配时显示。

#### identify（选填）
默认值： `auto`。

这是执行 `actions` 时的身份，其取值和作用如下表。
|`identify`|作用
|---|---
|`auto`|以当前输入 `/ccsr` 指令的人的身份执行 `actions`
|`console`|以控制台身份执行 `actions`
|`bypass`|玩家身份跳过权限检测执行 `actions`

#### result（选填）
默认值：`指令已执行`。

指令匹配成功并执行结束后，发送给指令发送者的信息。可以使用变量。

## 插件指令
所有格式是 `/ccs <config | run> <remain-arguments>` 的指令都可以被简化为 `/ccs<c|r> <remain-arguments>`，例如 `/ccs config` 可以简化为 `/ccsc`。`CCSC` 也就是 `CustomCommandS Config`， `CCSR` 是 `CustomCommandS Run`。

下表中的指令对应的权限见[权限节点](#权限节点)。

|指令|作用
|---|---
|`/ccs reload`|重载所有设置
|`/ccs version`|显示插件名、版本号等信息
|`/ccs debug`|开关调试模式
|`/ccsc list`|显示所有已加载的指令组
|`/ccsc add <group>`|新建指令组
|`/ccsc group <group>`|查看有关指令组`<group>`的信息
|`/ccsc remove <group>`|删除指令组`<group>`
|`/ccsc group <group> add <branch>`|在组中添加分支`<branch>`
|`/ccsc group <group> remove <branch>`|删除组中的分支`<branch>`
|`/ccsc group <group> rename <new-name>`|重命名指令组为`<new-name>`
|`/ccsc group <group> command <branch>`|查看分支`<branch>`的信息
|`/ccsc group <group> command <branch> rename <new-name>`|重命名分支`<branch>`为`<new-name>`
|`/ccsc group <group> command <branch> identify <identify>`|设置执行执行指令的身份为
|`/ccsc group <group> command <branch> format <format>`|设置该分支的参数格式
|`/ccsc group <group> command <branch> actions add <command>`|在该分支的执行指令中追加新的指令`<command>`
|`/ccsc group <group> command <branch> actions clear`|清空执行指令
|`/ccsc group <group> command <branch> actions remove <command>`|删除执行指令中的第一个指令`<command>`
|`/ccsc group <group> command <branch> actions set <command>`|设置执行指令为指令`<command>`
|`/ccsc group <group> command <branch> actions edit <index> <command>`|修改第`<index>`条执行指令为 `<command>`
|`/ccsc group <group> command <branch> result <string>`|修改返回信息为 `<string>`，可以包含空格或为空
|`/ccsc group <group> command <branch> usage <usage>`|修改指令用法为 `<usage>`，可以包含空格或为空
|`/ccsc group <group> command <branch> permissions add <permission>`|在该分支的匹配权限中增加新的权限节点 `<permission>`
|`/ccsc group <group> command <branch> permissions clear`|清除该分支的匹配权限。
|`/ccsc group <group> command <branch> permissions remove <permission>`|删除该分支的匹配权限中的权限节点 `<permission>`
|`/ccsc group <group> command <branch> permissions set <permission>`|设置匹配权限为 `<permission>`
|`/ccsc group <group> command <branch> permissions edit <index> <command>`|修改匹配权限中的第 `<index>` 个节点为 `<command>`
|`/ccsc group <group> command <branch> permissions default`|将匹配权限设置为默认值，即 `ccs.run.<group>.<branch>`
|`/ccsc val <variable-name>`|查找 `config.yml` 中的设置项 `<variable-name>`，并显示其值
|`/ccsc val <variable-name> <set-to>`|设置 `config.yml` 中的设置项 `<variable-name>` 的值为 `<set-to>`
|`/ccsr <branch> [arguments-list]`|执行在 `commands.yml` 内定义的指令 `<branch> `

相关扩展内容请阅览[高级功能](#高级功能)。

### 注意
1. 使用指令修改指令组名 / 分支名 / 分支的 `format`，将会自动修改该组所有分支 / 该分支下的所有 `usage`。
1. 上表 `<>` 包围的参数为必填参数，`[]` 包围的为选填参数，实际使用指令并不需要添加该括号。
1. 一个参数的括号中的 `|` 表示该处有多种写法。例如`<global|personal>` 表示此处必须写 `global` 或 `personal` 中的一个。
1. 所有的 `index` 均从 `1` 开始。


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
|`ccs.run.<指令组名>.<指令分支名>`|使用指令组 `指令组名` 中的 `指令分支名` 分支
|`ccs.config.val`|查看和设置一个项目
|`ccs.env.reload`|重载所有在线玩家的变量表
|`ccs.env.global`|编辑全局变量表
|`ccs.env.personal`|编辑玩家私人变量表

## 画大饼
欢迎提交 `issue` 反馈你希望增加的功能。

### 已经开发完成的功能
下面枚举的功能将在下一个版本更新时发布。
暂无

### 正在开发的功能
1. 使用正则表达式检查变量名。

### 可能要增加的功能
下面枚举的功能我有相关想法，但是不知道实际使用性如何，需要大家的反馈才会决定是否要开发。
1. 允许直接使用 `/<指令组名> <分支名> [参数列表]` 的形式执行指令，无需使用 `/ccsr`

## 高级功能
### 变量表机制
变量表是记录变量的值的一张表。

#### 变量表种类
`CCS` 的变量表分四种：

|变量表|该表中的变量|该表中变量的特点
|---|---|---
|`format`变量表|在`format`中使用`{变量名}`定义的变量|在解析输入时建立，执行结束后便不存在了
|私人变量表|玩家自定义的变量|长期存在，会被写入插件数据文件中。每一个玩家都有一个独立的私人变量表。
|公共变量表|自定义的变量|长期存在，会被写入插件数据文件中。
|消息变量表|语言文件中定义的变量|不建议使用这里的变量，因为其值不确定

使用这三个表中的变量的方式，都是 `{变量名}`。其中私人和公共变量表存在的意义在于允许玩家自行定义一些不与 `format` 产生关联的变量。

#### 相关指令
|指令|作用
|---|---
|`/ccse global`|查看全服变量表中的变量
|`/ccse personal`|查看私人变量表中的变量
|`/ccse reload`|重新载入所有在线玩家的变量表
|`/ccse <global \| personal> set <variable> <value>`|设置变量表中的变量`<variable>`为<`value>`
|`/ccse <global \| personal> remove <variable>`|删除变量表中的变量`<variable>`
|`/ccse <global \| personal> clear`|删除变量表中所有的变量

#### 使用场景
插件 `PermissionEX` 对某一个权限组的操作指令都是以 `pex group <权限组名>` 开头的。我们可将这一串内容存入一个私人变量 `{prefix}` 中，下文便不需再写 `pex group <权限组名>`。例如：

````yaml
commands:
  group:
    branch1:
      # ...
      actions:
      - 'ccse personal set prefix pex group {group_name}'   # 在当前玩家的私人变量表中设置变量 {prefix} 的值为 pex group {group_name}
      - '{prefix} add <a-permission-node>'              # 下文便可以直接引用 {prefix} 了
      - '{prefix} remove <a-permission-node>'
      # - '{prefix} (其他的一些以 pex group {group_name} 开头的指令)'
      # ...
````

由于变量 `{prefix}` 位于玩家的私人变量表中，故同一时间不同玩家的 `{prefix}` 可以是不同的值。

更重要的是这个 `{prefix}` 还可以在其他 `actions` 中直接引用！例如：
````yaml
commands:
  pexgroup:
    into:
      format: 'into {group_name}'
      actions: 
      - 'ccse personal sey prefix pex group {group_name}'
    remain:
      format: '{remain}'
      actions:
      - '{prefix} {remain}'
    out:
      format: 'out'
      actions:
      - 'ccse personal remove prefix'
````
你可以使用 `/ccsr pexgroup into <权限组名>` 先设定下文操作的权限组，随后的 `/ccsr pexgroup <任何内容>`，都将被自动转化为 `/pex group <那个权限组名> <刚才的任何内容>`。除非输入 `/ccsr pexgroup out` 取消。

这样使用，相当于进-出工作区，或者相当于先选择一个权限组，然后对其进行操作。

如果你有编程语言基础，很容易理解这个机制很像指针。先把指令中的关键字句设置到变量中，后文直接引用，可以更大程度地简化输入，灵活变通。

#### 查找变量的方式
当发现使用变量后，`CCS` 会按照下面的顺序寻找变量的值：
1. 在 `format` 中查找变量，如果找到便使用该值。
1. 在玩家的私人变量表中查找变量，如果找到便使用该值。
1. 在全服的公共变量表中查找变量，如果找到便使用该值。
1. 在消息变量表中查找变量，如果找到便使用该值。

若经历以上步骤还是无法找到变量，它将会保持 `{变量名}` 的原样。

### 模式匹配机制

#### 匹配权限
匹配某一个指令分支的权限。默认是 `[ ccs.run.<该分支所处组名>.<分支名> ]`。

它作为选填项，可以在 `commands.yml` 的分支下添加：
````yaml
commands:
  group:
    branch1:
      format: 'qwq'
      # actions:
      # - ...

      permissions:
      - ccs.run.group.branch1
      # ...
    branch2:
      format: 'qwq'
      # ...
      permissions:
      - ccs.run.group.branch2
````

只有玩家具有某一个分支的所有匹配权限时，才能匹配该分支。

例如，若玩家只具有 `ccs.run.group.branch2` 权限，则输入 `/ccsr group qwq`，虽然两个分支都能匹配，但因为匹配 `branch1` 需要权限节点 `ccs.run.group.branch1`，而玩家缺少该节点，所以 CCS 并不会检查玩家的输入能否匹配 `branch1`。最终结果是玩家输入只能匹配一个分支 `branch2`，指令成功执行。

如果你希望对同一个指令格式，拥有不同权限的人可以互不影响地执行不同的分支，使用 `permissions` 加以区分是很好的办法。

值得一提的是，如果你修改了 `permissions`，将以你指明的那一串权限节点为准。

#### 查找指令分支的方式
当用户输入 `/ccsr` 开头的指令，随后的第一个单词会被当做指令组名，剩余内容是参数列表。

`CCS` 首先查找是否存在该指令组。若找到，便会使用当前参数列表匹配每一个分支的 `format`。能够匹配的那些分支在下文被称为 `可匹配分支`。
1. 若没有任何可匹配分支，则告诉玩家没有任何可匹配的分支，显示玩家有权限匹配的那些分支的 `usage`。
1. 若有多个可匹配分支，但玩家都没有权限匹配，则告诉玩家缺少权限节点。
1. 若有多个可匹配分支，但玩家有权限匹配的只有一个，则匹配成功，执行 `actions`。
1. 若有多个可匹配分支，玩家也有权限匹配其中的多个，则告诉玩家当前参数格式具有二义性，显示玩家有权限匹配的那些分支的 `usage`。

## 联系方式
* QQ group（QQ 群）:  `1028582500`
* Author（作者）: `Chuanwise`
* E-mail（邮箱）: `chuanwise@qq.com`
* 明城京联合太学，保留所有权利。
* Taixue, All rights reserved.

## 更新日志
### 4.1
发布于 `2021年3月11日`
1. 删除了默认 `result`。不写 `result` 时，将不显示 `result` 内容。
1. 修复了一些已知问题。

### 4.0
发布于 `2021年3月6日`
1. 进一步完善了消息提示系统和 `zhcn.json` 汉化提示包。
1. 新增变量表机制，让使用者可以更方便地简化输入指令。
1. 修改了指令执行机制，使得 `actions` 靠前的语句可以对靠后的语句产生影响。
1. 增加了对默认变量的说明。
1. 增加了 `actions` 中的脚本。
1. 增加了 `bypass` 的 `identify`。

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
* `Favouritesc`
    1. 引导我入门了最基础的 `Minecraft` 服务器技术。
    1. 为 `CCS` 的改进提供了很多很好的建议。
* `Eric`：[@ExerciseBook](https://github.com/ExerciseBook)
* `One47`
* `Coloryr`