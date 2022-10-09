use std::ops::DerefMut;

use async_trait::async_trait;
use proc_qq::{
    MessageChainParseTrait, MessageContentTrait, MessageEvent, MessageSendToSourceTrait,
};
use rand::Rng;
use tokio::sync::OnceCell;

use simple_bot_macros::{action, make_action};

use crate::model::keyword::KeyWord as Model;
use crate::plugin::{Action, CommandPlugin, Plugin, RawPlugin};

pub struct KeyWord {
    keywords: OnceCell<Vec<Model>>,
    actions: Vec<Box<dyn Action>>,
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
                event
                    .send_message_to_source(reply.clone().parse_message_chain())
                    .await
                    .unwrap();
                done = true;
            }
        }
        Ok(done)
    }
}

impl CommandPlugin for KeyWord {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

impl KeyWord {
    pub fn new() -> Self {
        Self {
            keywords: OnceCell::new(),
            actions: vec![make_action!(add_keyword)],
        }
    }

    async fn load_keyword(&self) -> &Vec<Model> {
        self.keywords
            .get_or_init(|| async {
                let mut db = crate::RB.lock().await;
                Model::select_all(db.deref_mut())
                    .await
                    .expect("从数据库读取关键词失败")
            })
            .await
    }
}

#[action("/keyword add {chance} {keyword} {reply}")]
async fn add_keyword(
    event: &MessageEvent,
    chance: Option<i32>,
    keyword: Option<String>,
    reply: Option<String>,
) -> anyhow::Result<bool> {
    if chance.is_none() || keyword.is_none() || reply.is_none() {
        event
            .send_message_to_source("参数不足".parse_message_chain())
            .await
            .unwrap();
        return Ok(false);
    }
    let event = event.as_group_message().unwrap();
    let chance = chance.unwrap();
    let keyword = keyword.unwrap();
    let reply = reply.unwrap();
    let mut db = crate::RB.lock().await;
    let keyword = Model {
        id: None,
        group_id: event.inner.group_code,
        regex: Some(keyword),
        reply: Some(reply),
        chance,
    };
    let result = Model::insert(db.deref_mut(), &keyword).await;
    if result.is_err() {
        event
            .send_message_to_source("添加失败".parse_message_chain())
            .await
            .unwrap();
        return Ok(false);
    }
    event
        .send_message_to_source("添加成功".parse_message_chain())
        .await
        .unwrap();
    Ok(true)
}
