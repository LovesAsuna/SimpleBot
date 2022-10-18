## Bot

一个基于 [RICQ](https://github.com/lz1998/ricq) 开发的机器人

[![Rust](https://github.com/LovesAsuna/SimpleBot/workflows/Rust/badge.svg)](https://github.com/LovesAsuna/SimpleBot/actions)
[![LICENSE](https://img.shields.io/github/license/LovesAsuna/SimpleBot.svg?style=popout)](https://github.com/LovesAsuna/SimpleBot/blob/master/LICENSE)
[![Issues](https://img.shields.io/github/issues/LovesAsuna/SimpleBot.svg?style=popout)](https://github.com/SLovesAsuna/SimpleBot/issues)
[![Release](https://img.shields.io/github/v/release/LovesAsuna/SimpleBot?include_prereleases)](https://github.com/LovesAsuna/SimpleBot/releases)

```
交流群: 787049553
```

## 🎉 功能

### 易于开发的插件系统

#### RawPlugin(原始插件)

> 提供原始的MessageEvent供用户自行使用

```rust
#[async_trait]
impl RawPlugin for Repeater {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        event.send_message_to_source("Hello World!".parse_message_chain()).await.unwrap();
        Ok(true)
    }
}
```

#### CommandPlugin(命令式插件)

> 用户提供注解编写预设的命令，插件系统往方法中注入参数，供用户使用

```rust
pub struct Hello {
    actions: Vec<Box<dyn Action>>,
}

impl Hello {
    pub fn new() -> Self {
        Remind {
            actions: vec![
                make_action!(hello)
            ]
        }
    }
}

#[async_trait]
impl CommandPlugin for Hello {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("hello {name}")]
async fn hello(event: &MessageEvent, name: Option<String>) -> anyhow::Result<bool> {
    if name.is_none() {
        return Ok(false);
    }
    event.send_message_to_source(format!("hello {}", name.unwrap()).parse_message_chain()).await.unwrap();
    Ok(true)
}
```

## 🕹️ 内置的插件

* 以图搜图
* 以图搜番
* 彩虹六号战绩查询
* bilibili UP主信息订阅
* Minecraft服务器查询
* 色图获取
* B站直播间消息订阅
* 百度百科
* 小鸡词典
* 关键词回复
* 一言
* 读懂世界
* 能不能好好说话
* 动图生成
* QQ群留言
* P站图片获取
* 复读机
* 杂项

## ☑ To-Do 列表

详见 [项目页面](https://github.com/LovesAsuna/SimpleBot/projects/1)

## 💽 如何使用

### 自编译

- 注意: 请使用 Cargo 打包

1.1. Clone 或者下载这个项目.

```bash
git clone https://github.com/LovesAsuna/SimpleBot.git
```

1.2. 编译

cargo run

### 直接下载

1. 到 [Release](https://github.com/LovesAsuna/SimpleBot/releases) 下载最新版本的 可执行文件
2. 根据对应的平台启动

## 📜 协议

[AGPL](https://github.com/LovesAsuna/SimpleBot/blob/master/LICENSE)

## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

特别感谢 [JetBrains](https://www.jetbrains.com/) 为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/) 等 IDE 的授权  
[![JetBrains](https://avatars.githubusercontent.com/u/878437?s=200&v=4)](https://www.jetbrains.com/)