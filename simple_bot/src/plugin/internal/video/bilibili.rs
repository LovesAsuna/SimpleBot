use crate::plugin::Plugin;
use proc_qq::re_exports::async_trait::async_trait;
use proc_qq::re_exports::ricq_core::msg::MessageChainBuilder;
use proc_qq::*;
use std::future::Future;
use std::pin::Pin;

pub struct BilibiliVideo {
    av_pattern: regex::Regex,
    bv_pattern: regex::Regex,
    short_pattern: regex::Regex,
    client: reqwest::Client,
}

impl BilibiliVideo {
    pub fn new() -> Self {
        let mut builder = reqwest::ClientBuilder::new();
        builder = builder.redirect(reqwest::redirect::Policy::none());
        let client = builder.build().unwrap();
        BilibiliVideo {
            av_pattern: regex::Regex::new("[aA][vV]\\d*").unwrap(),
            bv_pattern: regex::Regex::new("BV(\\d|[a-z]|[A-Z]){10}").unwrap(),
            short_pattern: regex::Regex::new("b23.tv/(\\w+)").unwrap(),
            client,
        }
    }
}

impl Plugin for BilibiliVideo {
    fn get_name(&self) -> &str {
        "B站视频解析"
    }

    fn get_desc(&self) -> &str {
        "对消息中的av号和bv号解析，返回视频的详细信息"
    }
}

pub(super) fn handlers() -> Vec<ModuleEventHandler> {
    vec![ModuleEventHandler {
        name: "BilibiliVideo".to_owned(),
        process: ModuleEventProcess::Message(Box::new(BilibiliVideo::new())),
    }]
}

#[async_trait]
impl MessageEventProcess for BilibiliVideo {
    async fn handle(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        let text = event.message_content();
        let tuple = self.parse_api(text).await;
        if tuple.is_none() {
            return Ok(false);
        }
        let tuple = tuple.unwrap();
        let response = reqwest::get(tuple.0).await?;
        let line = response.text().await.unwrap();
        if !line.starts_with("{\"code\":0") {
            return Ok(false);
        }
        let node: serde_json::Value = serde_json::from_str(&line)?;
        let data = &node["data"];
        if data.is_null() {
            return Ok(false);
        }
        let pic = data["pic"].as_str().unwrap_or("");
        let title = data["title"].as_str().unwrap_or("");
        let up = data["owner"]["name"].as_str().unwrap_or("");
        let uplink = &data["owner"]["mid"].as_u64().unwrap_or(0).to_string();
        let zone = data["tname"].as_str().unwrap_or("");
        let stat = &data["stat"];
        let view = &stat["view"].as_u64().unwrap_or(0).to_string();
        let barrage = &stat["danmaku"].as_u64().unwrap_or(0).to_string();
        let reply = &stat["reply"].as_u64().unwrap_or(0).to_string();
        let fav = &stat["favorite"].as_u64().unwrap_or(0).to_string();
        let coin = &stat["coin"].as_u64().unwrap_or(0).to_string();
        let share = &stat["share"].as_u64().unwrap_or(0).to_string();
        let like = &stat["like"].as_u64().unwrap_or(0).to_string();
        let desc = data["desc"].as_str().unwrap_or("");
        let mut builder = String::from("\n".to_string() + title);
        builder.push_str("\nUP: ");
        builder.push_str(up);
        builder.push_str("(https://space.bilibili.com/");
        builder.push_str(uplink);
        builder.push_str(")\n分区: ");
        builder.push_str(zone);
        builder.push_str("\n播放量: ");
        builder.push_str(view);
        builder.push_str(" 弹幕: ");
        builder.push_str(barrage);
        builder.push_str(" 评论: ");
        builder.push_str(reply);
        builder.push_str("\n收藏: ");
        builder.push_str(fav);
        builder.push_str(" 投币: ");
        builder.push_str(coin);
        builder.push_str(" 分享: ");
        builder.push_str(share);
        builder.push_str(" 点赞: ");
        builder.push_str(like);
        builder.push_str("\n");
        builder.push_str(desc);
        let mut reply_message = MessageChainBuilder::new().build();
        reply_message = reply_message
            .append(format!("链接: https://www.bilibili.com/video/{}", tuple.1).parse_text());
        let bytes = reqwest::get(pic)
            .await?
            .error_for_status()?
            .bytes()
            .await?
            .to_vec();
        let upload_res = event.upload_image_to_source(bytes).await;
        reply_message = match upload_res {
            Ok(image) => reply_message.append(image),
            Err(_) => reply_message.append("上传图片出错".parse_text()),
        };
        reply_message = reply_message.append(builder.parse_text());
        if event.send_message_to_source(reply_message).await.is_ok() {
            Ok(true)
        } else {
            Ok(false)
        }
    }
}

impl BilibiliVideo {
    fn parse_api(
        &self,
        text: String,
    ) -> Pin<Box<dyn Future<Output = Option<(String, String)>> + Send + '_>> {
        Box::pin(async move {
            if text.contains("av") {
                if let Some(capture) = self.av_pattern.captures(&text) {
                    let av = capture.get(0).unwrap().as_str();
                    return Some((
                        format!("https://api.bilibili.com/x/web-interface/view?aid={}", av),
                        av.to_string(),
                    ));
                }
            }
            if text.contains("BV") {
                if let Some(capture) = self.bv_pattern.captures(&text) {
                    let bv = capture.get(0).unwrap().as_str();
                    return Some((
                        format!("https://api.bilibili.com/x/web-interface/view?bvid={}", bv),
                        bv.to_string(),
                    ));
                }
            }
            if text.contains("b23.tv") {
                if let Some(capture) = self.short_pattern.captures(&text) {
                    let req_url = format!("https://b23.tv/{}", capture.get(1).unwrap().as_str());
                    let result = self.client.get(req_url).send().await;
                    if result.is_err() {
                        return None;
                    }
                    let resp = result.unwrap();
                    let content = resp
                        .headers()
                        .get("location")
                        .map(|s| String::from_utf8_lossy(s.as_bytes()).to_string());
                    return match content {
                        Some(content) => self.parse_api(content).await,
                        None => None,
                    };
                }
            }
            None
        })
    }
}
