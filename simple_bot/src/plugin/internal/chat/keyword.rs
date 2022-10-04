use std::ops::DerefMut;
use async_trait::async_trait;
use proc_qq::{MessageChainParseTrait, MessageContentTrait, MessageEvent, MessageSendToSourceTrait};
use rand::Rng;
use tokio::sync::OnceCell;

use crate::model::keyword::KeyWord as Model;
use crate::plugin::{Plugin, RawPlugin};

pub struct KeyWord {
    keywords: OnceCell<Vec<Model>>
}

impl Plugin for KeyWord {
    fn get_name(&self) -> &str {
        "关键词回复"
    }

    fn get_desc(&self) -> &str {
        "通过关键词，一定几率触发特定回复"
    }
}

#[async_trait]
impl RawPlugin for KeyWord {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        let content = event.message_content();
        let mut done = false;
        for keyword in self.load_keyword().await {
            let regex = regex::Regex::new(keyword.regex.as_ref().unwrap_or(&String::new()));
            if regex.is_err() {
                continue;
            }
            let regex = regex.unwrap();
            if !regex.is_match(&content) {
                continue;
            }
            let point = rand::thread_rng().gen_range(0..=100);
            if point > keyword.chance {
                continue;
            }
            if let Some(reply) = &keyword.reply {
                event.send_message_to_source(reply.clone().parse_message_chain()).await.unwrap();
                done = true;
            }
        }
        Ok(done)
    }
}

impl KeyWord {
    pub fn new() -> Self {
        Self {
            keywords: OnceCell::new()
        }
    }

    async fn load_keyword(&self) -> &Vec<Model> {
        self.keywords.get_or_init(|| async {
            let mut db = crate::RB.lock().await;
            Model::select_all(db.deref_mut()).await.expect("从数据库读取关键词失败")
        }).await
    }
}