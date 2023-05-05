use proc_qq::event;
use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};

use crate::plugin::Plugin;

pub struct Baidu;

impl Plugin for Baidu {
    fn get_name(&self) -> &str {
        "百度百科"
    }

    fn get_desc(&self) -> &str {
        "百度百科"
    }
}

#[event(bot_command = "/baike {content}")]
async fn search(event: &MessageEvent, content: String) -> anyhow::Result<bool> {
    let url = format!("https://baike.baidu.com/item/{}", content.unwrap());
    let resp = reqwest::get(url).await?;
    let text = resp.text().await?;
    let content = parse_content(&text);
    match content {
        None => Ok(false),
        Some(content) => {
            event
                .send_message_to_source(content.parse_message_chain())
                .await
                .unwrap();
            Ok(true)
        }
    }
}

fn parse_content(content: &String) -> Option<String> {
    visdom::Vis::load(content).ok().and_then(|dom| {
        dom.find("meta[name=description]")
            .attr("content")
            .map(|attr| attr.to_string())
    })
}
