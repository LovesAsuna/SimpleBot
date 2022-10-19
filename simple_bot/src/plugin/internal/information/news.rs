use crate::plugin::{Action, CommandPlugin, Plugin};
use async_trait::async_trait;
use proc_qq::{MessageChainAppendTrait, MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};
use simple_bot_macros::{action, make_action};
use std::collections::HashMap;
use std::fmt::{Display, Formatter};
use proc_qq::re_exports::ricq_core::msg::MessageChainBuilder;

pub struct News {
    actions: Vec<Box<dyn Action>>,
}

impl News {
    pub fn new() -> Self {
        Self {
            actions: vec![make_action!(new)],
        }
    }
}

impl Plugin for News {
    fn get_name(&self) -> &str {
        "每日新闻"
    }

    fn get_desc(&self) -> &str {
        "每天60秒读懂世界"
    }
}

#[async_trait]
impl CommandPlugin for News {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("/60s")]
async fn new(event: &MessageEvent) -> anyhow::Result<bool> {
    let url = "https://api.03c3.cn/zb/";
    let resp = reqwest::get(url).await?;
    let bytes = resp.bytes().await?;
    let image = event.upload_image_to_source(bytes.to_vec()).await?;
    let message = MessageChainBuilder::new()
        .build()
        .append(image);
    event.send_message_to_source(message).await.unwrap();
    Ok(true)
}
