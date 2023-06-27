use std::time::Duration;

use proc_qq::re_exports::ricq::msg::elem::RQElem;
use proc_qq::re_exports::ricq::msg::MessageChain;
use proc_qq::{
    event, MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait, ModuleEventHandler,
};

use crate::future::WaitForMessage;
use crate::plugin::internal::picture::ascii2d::Ascii2d;
use crate::plugin::internal::picture::saucenao::SauceNao;
use crate::plugin::internal::picture::search_source::SearchSource;
use crate::plugin::Plugin;

pub struct Search;

unsafe impl Send for Search {}
unsafe impl Sync for Search {}

impl Plugin for Search {
    fn get_name(&self) -> &str {
        "二次元图片搜索"
    }

    fn get_desc(&self) -> &str {
        ""
    }
}

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![search {}.into()]
}

#[event(bot_command = "/搜图 {source_type}")]
async fn search(event: &MessageEvent, source_type: usize) -> anyhow::Result<bool> {
    let source_type = source_type;
    let search_source = select_source(source_type);
    if search_source.is_none() {
        return Ok(false);
    }
    let search_source = search_source.unwrap();
    event
        .send_message_to_source("请在五秒内发送图片！".parse_message_chain())
        .await
        .unwrap();
    let image = event.wait_for_message(Duration::from_secs(5)).await;
    if image.is_none() {
        event
            .send_message_to_source("未接收到图片消息，放弃搜索！".parse_message_chain())
            .await
            .unwrap();
        return Ok(false);
    }
    for element in image.unwrap().into_iter() {
        match element {
            RQElem::GroupImage(image) => {
                let url = image.url();
                event
                    .send_message_to_source(
                        format!("{}搜索中，请稍后...", search_source.get_name())
                            .parse_message_chain(),
                    )
                    .await
                    .unwrap();
                let res = search_source.search(url).await;
                match res {
                    Ok(res) => {
                        if res.len() == 0 {
                            event
                                .send_message_to_source("未搜索到结果".parse_message_chain())
                                .await
                                .unwrap();
                            return Ok(true);
                        }
                        for result in res {
                            let thumbnail = result.get_thumbnail();
                            let u8 = reqwest::get(thumbnail)
                                .await
                                .ok()
                                .unwrap()
                                .bytes()
                                .await
                                .unwrap();
                            let mut message_chain = MessageChain::default();
                            let image = event.upload_image_to_source(u8).await.unwrap();
                            message_chain.push(image);
                            let mut builder = format!(
                                "相似度: {}\n画师名: {}\n相关链接:\n",
                                result.get_similarity(),
                                result.get_member_name()
                            );
                            for url in result.get_ext_urls() {
                                builder = builder + url + "\n";
                            }
                            message_chain.push(
                                proc_qq::re_exports::ricq_core::msg::elem::Text::new(builder),
                            );
                            event.send_message_to_source(message_chain).await.unwrap();
                            return Ok(true);
                        }
                    }
                    Err(err) => {
                        event
                            .send_message_to_source(err.to_string().parse_message_chain())
                            .await
                            .unwrap();
                        return Ok(false);
                    }
                }
            }
            _ => {}
        }
    }
    event
        .send_message_to_source("未接收到图片消息，放弃搜索！".parse_message_chain())
        .await
        .unwrap();
    Ok(false)
}

fn select_source(source_type: usize) -> Option<Box<dyn SearchSource>> {
    match source_type {
        1 => Some(Box::new(SauceNao)),
        2 => Some(Box::new(Ascii2d)),
        _ => None,
    }
}
