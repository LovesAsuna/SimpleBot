use std::time::Duration;

use proc_qq::event;
use proc_qq::re_exports::ricq::msg::MessageChain;
use proc_qq::re_exports::ricq_core::msg::elem::At;
use proc_qq::re_exports::ricq_core::msg::MessageChainBuilder;
use proc_qq::{
    GroupMessageEvent, MessageChainAppendTrait, MessageEvent, MessageSendToSourceTrait,
    ModuleEventHandler, ModuleEventProcess, TextEleParseTrait,
};

use crate::plugin::Plugin;

pub struct Remind;

impl Plugin for Remind {
    fn get_name(&self) -> &str {
        "提醒"
    }

    fn get_desc(&self) -> &str {
        "一定时间后做出提醒"
    }
}

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![ModuleEventHandler {
        name: "Remind".to_owned(),
        process: ModuleEventProcess::Message(Box::new(remind {})),
    }]
}

#[event(bot_command = "{time}分钟后提醒我{content}")]
async fn remind(event: &MessageEvent, time: u64, content: String) -> anyhow::Result<bool> {
    let event = event.as_group_message()?;
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
