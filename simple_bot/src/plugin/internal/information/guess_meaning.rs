use std::collections::HashMap;

use proc_qq::{event, MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};

use crate::plugin::Plugin;

pub struct GuessMeaning;
impl Plugin for GuessMeaning {
    fn get_name(&self) -> &str {
        "能不能好好说话"
    }

    fn get_desc(&self) -> &str {
        "按拼音首字母猜意思"
    }
}

#[event(bot_command = "/nbnhhsh {content}")]
async fn search(event: &MessageEvent, content: String) -> anyhow::Result<bool> {
    let url = "https://lab.magiconch.com/api/nbnhhsh/guess";
    let mut request = reqwest::ClientBuilder::new().build().unwrap().post(url);
    request = request.json(&Into::<HashMap<_, _>>::into([("text", content.unwrap())]));
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
    serde_json::from_str::<serde_json::Value>(content)
        .ok()
        .and_then(|v| {
            v.as_array().and_then(|v| {
                let trans = &v[0]["trans"];
                if trans.is_null() {
                    None
                } else {
                    Some(trans.to_string())
                }
            })
        })
}
