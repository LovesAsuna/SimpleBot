use async_trait::async_trait;
use std::ops::DerefMut;

use proc_qq::{
    event, MessageChainParseTrait, MessageContentTrait, MessageEvent, MessageEventProcess,
    MessageSendToSourceTrait, ModuleEventHandler, ModuleEventProcess,
};
use rand::Rng;
use tokio::sync::OnceCell;

use crate::model::keyword::KeyWord as Model;
use crate::plugin::Plugin;

pub struct KeyWord {
    keywords: OnceCell<Vec<Model>>,
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
impl MessageEventProcess for KeyWord {
    async fn handle(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        let event = event.as_group_message()?;
        let content = event.message_content();
        let mut done = false;
        for keyword in self.load_keyword().await {
            let group = event.inner.group_code;
            if keyword.group_id != group && keyword.group_id != 0 {
                continue;
            }
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

impl KeyWord {
    pub fn new() -> Self {
        Self {
            keywords: OnceCell::new(),
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

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![
        ModuleEventHandler {
            name: "KeyWord".to_owned(),
            process: ModuleEventProcess::Message(Box::new(KeyWord::new())),
        },
        add_keyword {}.into(),
    ]
}

#[event(bot_command = "/keyword add {chance} {keyword} {reply}")]
pub(super) async fn add_keyword(
    event: &MessageEvent,
    chance: i32,
    keyword: String,
    reply: String,
) -> anyhow::Result<bool> {
    let event = event.as_group_message().unwrap();
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
