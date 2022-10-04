use std::collections::HashMap;
use std::fmt::{Display, Formatter};
use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};
use simple_bot_macros::{action, make_action};
use crate::plugin::{Action, CommandPlugin, Plugin};
use async_trait::async_trait;

pub struct Jikipedia {
    actions: Vec<Box<dyn Action>>,
}

impl Jikipedia {
    pub fn new() -> Self {
        Self {
            actions: vec![
                make_action!(search)
            ]
        }
    }
}

impl Plugin for Jikipedia {
    fn get_name(&self) -> &str {
        "小鸡词典"
    }

    fn get_desc(&self) -> &str {
        "可以查梗的网络词典"
    }
}

#[async_trait]
impl CommandPlugin for Jikipedia {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("/查梗 {content}")]
async fn search(event: &MessageEvent, content: Option<String>) -> anyhow::Result<bool> {
    if content.is_none() {
        return Ok(false);
    }
    let url = "https://api.jikipedia.com/go/auto_complete";
    let mut request = reqwest::ClientBuilder::new().build().unwrap().post(url);
    request = request.header("Client", "Web");
    request = request.json(&Into::<HashMap<_, _>>::into([("phrase", content.unwrap())]));
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
    let root: serde_json::Value = serde_json::from_str(content).unwrap();
    let array = root["data"].as_array().unwrap();
    for node in array {
        let entities = node["entities"].as_array();
        if let Some(entities) = entities {
            if entities.len() == 0 {
                continue;
            }
            return Some(format!("{}", Entity::new(&entities[0])))
        }
    }
    None
}

struct Entity {
    title: String,
    content: String,
}

impl Entity {
    fn new(value: &serde_json::Value) -> Self {
        Entity {
            title: value["title"].as_str().unwrap().to_string(),
            content: value["content"].as_str().unwrap().to_string(),
        }
    }
}

impl Display for Entity {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "定义: [{}]\n{}", self.title, self.content)
    }
}