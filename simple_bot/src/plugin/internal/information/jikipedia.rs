use crate::plugin::Plugin;
use proc_qq::{
    event, MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait, ModuleEventHandler,
};
use std::collections::HashMap;
use std::fmt::{Display, Formatter};

pub struct Jikipedia;

impl Plugin for Jikipedia {
    fn get_name(&self) -> &str {
        "小鸡词典"
    }

    fn get_desc(&self) -> &str {
        "可以查梗的网络词典"
    }
}

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![search {}.into()]
}

#[event(bot_command = "/查梗 {content}")]
async fn search(event: &MessageEvent, content: String) -> anyhow::Result<bool> {
    let url = "https://api.jikipedia.com/go/auto_complete";
    let mut request = reqwest::ClientBuilder::new().build().unwrap().post(url);
    request = request.header("Client", "Web");
    request = request.json(&Into::<HashMap<_, _>>::into([("phrase", content)]));
    let resp = request.send().await?;
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
    let root: serde_json::Value = serde_json::from_str(content).unwrap();
    let array = root["data"].as_array().unwrap();
    for node in array {
        let entities = node["entities"].as_array();
        if let Some(entities) = entities {
            if entities.len() == 0 {
                continue;
            }
            return Some(format!("{}", Entity::new(&entities[0])));
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
