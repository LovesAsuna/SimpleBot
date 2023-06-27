use std::collections::HashSet;
use std::ops::SubAssign;
use std::time::Duration;

use crate::plugin::Plugin;
use chrono::prelude::*;
use lazy_static::lazy_static;
use proc_qq::re_exports::ricq::msg::MessageChain;
use proc_qq::re_exports::ricq_core::msg::MessageChainBuilder;
use proc_qq::{
    event, MessageChainAppendTrait, MessageChainParseTrait, MessageEvent, MessageRecallTrait,
    MessageSendToSourceTrait, ModuleEventHandler, TextEleParseTrait,
};

pub struct PixivProxy;

impl Plugin for PixivProxy {
    fn get_name(&self) -> &str {
        "Pixiv图片获取"
    }

    fn get_desc(&self) -> &str {
        "通过反代理Pixiv获取到作品的信息"
    }
}

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![get {}.into()]
}

#[event(bot_command = "/pixiv work {id}")]
async fn get(event: &MessageEvent, id: String) -> anyhow::Result<bool> {
    let api = format!("https://api.obfs.dev/api/pixiv/illust?id={id}");
    let resp = tokio::time::timeout(Duration::from_secs(15), reqwest::get(api)).await;
    if resp.is_err() {
        event
            .send_message_to_source("Error: 获取图像信息超时".parse_message_chain())
            .await
            .unwrap();
        return Ok(false);
    }
    let text = resp.unwrap()?.text().await?;
    let root: serde_json::Value = serde_json::from_str(&text)?;
    if let serde_json::value::Value::Object(err) = &root["error"] {
        let message = String::from("message");
        let user_message = String::from("user_message");
        let mut text = err[&message].as_str().unwrap_or("");
        if text.is_empty() {
            text = err[&user_message].as_str().unwrap_or("");
        }
        event
            .send_message_to_source(format!("Error: {}", text).parse_message_chain())
            .await
            .unwrap();
        return Ok(false);
    }
    let illustration = &root["illust"];
    let title = illustration["title"].as_str().unwrap_or("");
    let user_name = illustration["user"]["name"].as_str().unwrap_or("");
    let account = illustration["user"]["account"].as_str().unwrap_or("");
    let mut time = DateTime::parse_from_rfc3339(illustration["create_date"].as_str().unwrap())?;
    let format = format!(
        "%Y年%m月%d日{}%H点%M分",
        if time.hour() <= 12 {
            "上午"
        } else if time.hour() <= 18 {
            time.sub_assign(chrono::Duration::hours(12));
            "下午"
        } else {
            time.sub_assign(chrono::Duration::hours(12));
            "晚上"
        }
    );
    let time_text = time.format(&format).to_string();
    let tags = flatten_tags(&illustration["tags"]);
    let r18 = validate(&tags);
    let count = illustration["page_count"].as_u64().unwrap_or(0);
    let view = illustration["total_view"].as_u64().unwrap_or(0);
    let mark = illustration["total_bookmarks"].as_u64().unwrap_or(0);
    //build message
    let mut builder = MessageChainBuilder::new().build();
    builder = builder.append(
        format!(
            r"[pid{id}]
https://www.pixiv.net/artworks/{id}
原图:",
        )
        .parse_text(),
    );
    if count == 1 {
        builder = upload_image(event, builder, &id).await.unwrap();
    } else {
        builder = builder.append(
            format!(
                "该作品共有{count}张图片{}",
                if count > 3 { "，预览前3张" } else { "" }
            )
            .parse_text(),
        );
        for i in 1..=count {
            builder = upload_image(event, builder, &format!("{id}-{i}"))
                .await
                .unwrap();
        }
    }
    builder = builder.append(
        format!(
            r"标题: {title}
画师: {user_name}＠{account}
投稿时间: {time_text}
收藏数: {mark}
查看数: {view}
R18: {r18}
直连链接: https://pixiv.re/{id}.jpg",
            id = if count == 1 {
                id
            } else {
                format!("{}{{1-{}}}", id, count)
            }
        )
        .parse_text(),
    );
    let receipt = event.send_message_to_source(builder).await.unwrap();
    if r18 {
        tokio::time::sleep(Duration::from_secs(5)).await;
        event.recall(receipt).await.unwrap();
    }
    Ok(true)
}

fn flatten_tags(tags: &serde_json::Value) -> HashSet<&str> {
    let mut set = HashSet::new();
    let array = tags.as_array();
    match array {
        None => {}
        Some(items) => {
            for item in items {
                let name = item["name"].as_str();
                if name.is_some() {
                    set.insert(name.unwrap());
                }
            }
        }
    }
    set
}

lazy_static! {
    static ref R18_PATTERN: regex::Regex = regex::Regex::new("R-[1-9]+").unwrap();
}

fn validate(tags: &HashSet<&str>) -> bool {
    tags.iter().any(|tag| R18_PATTERN.is_match(tag))
}

async fn upload_image(
    event: &MessageEvent,
    mut builder: MessageChain,
    image_id: &String,
) -> anyhow::Result<MessageChain> {
    let bytes = &reqwest::get(format!("https://pixiv.re/{image_id}.jpg"))
        .await?
        .bytes()
        .await?;
    let image = tokio::time::timeout(
        Duration::from_secs(60),
        event.upload_image_to_source(bytes.to_vec()),
    )
    .await;
    if image.is_err() {
        builder = builder.append(
            "\n图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者\n".parse_text(),
        );
        return Ok(builder);
    }
    let image = image.unwrap();
    builder = match image {
        Err(_) => builder,
        Ok(image) => builder.append(image),
    };
    Ok(builder)
}
