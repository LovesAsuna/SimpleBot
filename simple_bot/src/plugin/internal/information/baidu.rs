use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};
use simple_bot_macros::{action, make_action};
use crate::plugin::{Action, CommandPlugin, Plugin};
use async_trait::async_trait;

pub struct Baidu {
    actions: Vec<Box<dyn Action>>,
}

impl Baidu {
    pub fn new() -> Self {
        Self {
            actions: vec![
                make_action!(search)
            ]
        }
    }
}

impl Plugin for Baidu {
    fn get_name(&self) -> &str {
        "百度百科"
    }

    fn get_desc(&self) -> &str {
        "百度百科"
    }
}

#[async_trait]
impl CommandPlugin for Baidu {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("/baike {content}")]
async fn search(event: &MessageEvent, content: Option<String>) -> anyhow::Result<bool> {
    if content.is_none() {
        return Ok(false);
    }
    let url = format!("https://baike.baidu.com/item/{}", content.unwrap());
    let resp = reqwest::get(url).await?;
    let text = resp.text().await?;
    let content = parse_content(&text);
    match content {
        None => {
            Ok(false)
        },
        Some(content) => {
            event.send_message_to_source(content.parse_message_chain()).await.unwrap();
            Ok(true)
        }
    }
}

fn parse_content(content: &String) -> Option<String> {
    visdom::Vis::load(content).ok().and_then(
        |dom|  dom.find("meta[name=description]").attr("content").map(|attr| attr.to_string())
    )
}