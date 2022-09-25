use std::time::Duration;

use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait, TextEleParseTrait};
use proc_qq::re_exports::async_trait::async_trait;
use proc_qq::re_exports::ricq::msg::elem::RQElem;
use proc_qq::re_exports::ricq::msg::{MessageChain};

use simple_bot_macros::action;
use simple_bot_macros::make_action;

use crate::future::WaitForMessage;
use crate::plugin::{Action, CommandPlugin};
use crate::plugin::internal::picture::saucenao::SauceNao;
use crate::plugin::internal::picture::search_source::SearchSource;
use crate::plugin::Plugin;

pub struct Search {
    actions: Vec<Box<dyn Action>>,
}

unsafe impl Send for Search{}
unsafe impl Sync for Search{}

impl Plugin for Search {
    fn get_name(&self) -> &str {
        "二次元图片搜索"
    }

    fn get_desc(&self) -> &str {
        ""
    }
}

#[async_trait]
impl CommandPlugin for Search {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

impl Search {
    pub fn new() -> Self {
        Self {
            actions: vec![
                make_action!(search)
            ]
        }
    }
}

#[action("/搜图 {source_type}")]
async fn search(event: &MessageEvent, source_type: Option<usize>) -> anyhow::Result<bool> {
    if source_type.is_none() {
        return Ok(false);
    }
    let source_type = source_type.unwrap();
    let search_source = select_source(source_type);
    if search_source.is_none() {
        return Ok(false);
    }
    let search_source = search_source.unwrap();
    event.send_message_to_source("请在五秒内发送图片！".parse_message_chain()).await.unwrap();
    let image = event.wait_for_message(Duration::from_secs(5)).await;
    if image.is_none() {
        event.send_message_to_source("未接收到图片消息，放弃搜索！".parse_message_chain()).await.unwrap();
        return Ok(false);
    }
    for element in image.unwrap().into_iter() {
        match element {
            RQElem::GroupImage(image) => {
                let url = image.url();
                event.send_message_to_source("搜索中，请稍后...".parse_message_chain()).await.unwrap();
                let res = search_source.search(url).await;
                match res {
                    Ok(res) => {
                        for result in res {
                            let thumbnail = result.get_thumbnail();
                            let u8 = reqwest::get(thumbnail).await.ok().unwrap().bytes().await.unwrap();
                            let mut message_chain = MessageChain::default();
                            let image = event.upload_image_to_source(u8).await.unwrap();
                            message_chain.push(image);
                            let mut builder = format!("相似度: {}\n画师名: {}\n相关链接:\n", result.get_similarity(), result.get_member_name());
                            for url in result.get_ext_urls() {
                                builder = builder + url + "\n";
                            }
                            message_chain.push(proc_qq::re_exports::ricq_core::msg::elem::Text::new(builder));
                            event.send_message_to_source(message_chain).await.unwrap();
                        }
                    },
                    Err(err) => {
                        event.send_message_to_source(err.to_string().parse_message_chain()).await.unwrap();
                    }
                }
            },
            _ => {}
        }
    }
    event.send_message_to_source("未接收到图片消息，放弃搜索！".parse_message_chain()).await.unwrap();
    Ok(true)
}

fn select_source(source_type: usize) -> Option<Box<dyn SearchSource>> {
    match source_type {
        1 => {
            Some(Box::new(SauceNao))
        }
        _ => None
    }
}