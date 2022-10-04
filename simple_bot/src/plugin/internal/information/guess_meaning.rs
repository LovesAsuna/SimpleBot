use std::collections::HashMap;
use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};
use simple_bot_macros::{action, make_action};
use crate::plugin::{Action, CommandPlugin, Plugin};
use async_trait::async_trait;

pub struct GuessMeaning {
    actions: Vec<Box<dyn Action>>,
}

impl GuessMeaning {
    pub fn new() -> Self {
        Self {
            actions: vec![
                make_action!(search)
            ]
        }
    }
}

impl Plugin for GuessMeaning {
    fn get_name(&self) -> &str {
        "能不能好好说话"
    }

    fn get_desc(&self) -> &str {
        "按拼音首字母猜意思"
    }
}

#[async_trait]
impl CommandPlugin for GuessMeaning {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("/nbnhhsh {content}")]
async fn search(event: &MessageEvent, content: Option<String>) -> anyhow::Result<bool> {
    if content.is_none() {
        return Ok(false);
    }
    let url = "https://lab.magiconch.com/api/nbnhhsh/guess";
    let mut request = reqwest::ClientBuilder::new().build().unwrap().post(url);
    request = request.json(&Into::<HashMap<_, _>>::into([("text", content.unwrap())]));
    let resp = request.send().await?;
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
    serde_json::from_str::<serde_json::Value>(content).ok().and_then(
        |v| {
            v.as_str().map(|s| s.to_owned())
        }
    )
}