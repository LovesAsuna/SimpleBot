use std::time::Duration;

use async_trait::async_trait;
use proc_qq::re_exports::ricq::msg::MessageChain;
use proc_qq::re_exports::ricq_core::msg::elem::At;
use proc_qq::re_exports::ricq_core::msg::MessageChainBuilder;
use proc_qq::{
    GroupMessageEvent, MessageChainAppendTrait, MessageEvent, MessageSendToSourceTrait,
    TextEleParseTrait,
};

use simple_bot_macros::{action, make_action};

use crate::plugin::{Action, CommandPlugin, Plugin};

pub struct Remind {
    actions: Vec<Box<dyn Action>>,
}

impl Remind {
    pub fn new() -> Self {
        Remind {
            actions: vec![make_action!(remind)],
        }
    }
}

impl Plugin for Remind {
    fn get_name(&self) -> &str {
        "提醒"
    }

    fn get_desc(&self) -> &str {
        "一定时间后做出提醒"
    }
}

#[async_trait]
impl CommandPlugin for Remind {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("{time}分钟后提醒我{content}")]
async fn remind(
    event: &MessageEvent,
    time: Option<u64>,
    content: Option<String>,
) -> anyhow::Result<bool> {
    let event = event.as_group_message()?;
    if time.is_none() || content.is_none() {
        return Ok(false);
    }
    let time = time.unwrap();
    let content = content.unwrap();
    let message = build_content(event, format!("我将在{}分钟后提醒你{} ", time, content));
    event.send_message_to_source(message).await.unwrap();
    tokio::time::sleep(Duration::from_secs(60 * time)).await;
    let message = build_content(event, content);
    event.send_message_to_source(message).await.unwrap();
    Ok(true)
}

fn build_content(event: &GroupMessageEvent, content: String) -> MessageChain {
    let message = MessageChainBuilder::new().build();
    message
        .append(At::new(event.inner.from_uin))
        .append(format!(" {}", content).parse_text())
}
