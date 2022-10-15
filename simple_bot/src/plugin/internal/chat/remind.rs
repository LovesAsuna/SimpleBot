use std::time::Duration;

use async_trait::async_trait;
use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};

use simple_bot_macros::{action, make_action};

use crate::plugin::{Action, CommandPlugin, Plugin};

pub struct Remind {
    actions: Vec<Box<dyn Action>>
}

impl Remind {
    pub fn new() -> Self {
        Remind {
            actions: vec![
                make_action!(remind)
            ]
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
async fn remind(event: &MessageEvent, time: Option<u64>, content: Option<String>) -> anyhow::Result<bool> {
    if time.is_none() || content.is_none() {
        return Ok(false);
    }
    let time = time.unwrap();
    let content = content.unwrap();
    tokio::time::sleep(Duration::from_secs(60 * time)).await;
    event.send_message_to_source(content.parse_message_chain()).await.unwrap();
    Ok(true)
}