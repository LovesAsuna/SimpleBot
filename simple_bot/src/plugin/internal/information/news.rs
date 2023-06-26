use crate::plugin::Plugin;
use proc_qq::re_exports::ricq_core::msg::MessageChainBuilder;
use proc_qq::{
    event, MessageChainAppendTrait, MessageEvent, MessageSendToSourceTrait, ModuleEventHandler,
};

pub struct News;

impl Plugin for News {
    fn get_name(&self) -> &str {
        "每日新闻"
    }

    fn get_desc(&self) -> &str {
        "每天60秒读懂世界"
    }
}

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![new {}.into()]
}

#[event(bot_command = "/60s")]
async fn new(event: &MessageEvent) -> anyhow::Result<bool> {
    let url = "https://api.03c3.cn/zb/";
    let resp = reqwest::get(url).await?;
    let bytes = resp.bytes().await?;
    let image = event.upload_image_to_source(bytes.to_vec()).await?;
    let message = MessageChainBuilder::new().build().append(image);
    event.send_message_to_source(message).await.unwrap();
    Ok(true)
}
