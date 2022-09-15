use std::collections::HashMap;
use std::time::Duration;

use async_trait::async_trait;
use lazy_static::lazy_static;
use proc_qq::{MessageEvent, MessageSendToSourceTrait, TextEleParseTrait};
use proc_qq::re_exports::*;
use proc_qq::re_exports::ricq::msg::{MessageChain, MessageChainBuilder};
use tokio::sync::Mutex;
use tokio::sync::oneshot::*;

lazy_static! {
    pub static ref SESSION_CACHE: Mutex<HashMap<i64, Sender<MessageChain>>> = Mutex::new(HashMap::new());
}

#[async_trait]
pub trait WaitForMessage {
    async fn wait_for_message(&self, timeout: Duration) -> Option<MessageChain>;
}

#[async_trait]
impl WaitForMessage for MessageEvent {
    async fn wait_for_message(&self, timeout: Duration) -> Option<MessageChain> {
        tokio::time::timeout(timeout, async {
            let uid = self.from_uin();
            let (tx, rx) = channel::<MessageChain>();
            {
                let mut cache = SESSION_CACHE.lock().await;
                if cache.contains_key(&uid) {
                    let mut builder = MessageChainBuilder::new();
                    builder.push(format!("{} are sill waiting.", uid).parse_text());
                    self.send_message_to_source(builder.build()).await.unwrap();
                    return None;
                }
                cache.insert(uid, tx);
            }
            rx.await.ok()
        }).await.unwrap_or(None)
    }
}