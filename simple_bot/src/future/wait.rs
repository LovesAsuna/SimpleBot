use std::collections::HashMap;
use std::time::Duration;

use lazy_static::lazy_static;
use proc_qq::re_exports::async_trait::async_trait;
use proc_qq::re_exports::ricq::msg::{MessageChain, MessageChainBuilder};
use proc_qq::{MessageEvent, MessageSendToSourceTrait, TextEleParseTrait};
use tokio::sync::oneshot::*;
use tokio::sync::Mutex;

lazy_static! {
    pub static ref SESSION_CACHE: Mutex<HashMap<i64, Sender<MessageChain>>> =
        Mutex::new(HashMap::new());
}

#[async_trait]
pub trait WaitForMessage {
    async fn wait_for_message(&self, timeout: Duration) -> Option<MessageChain>;
}

#[async_trait]
impl WaitForMessage for MessageEvent {
    async fn wait_for_message(&self, timeout: Duration) -> Option<MessageChain> {
        let (tx, rx) = channel::<MessageChain>();
        let uid = self.from_uin();
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
        let res = tokio::time::timeout(timeout, rx).await;
        {
            let mut cache = SESSION_CACHE.lock().await;
            cache.remove(&uid);
        }
        match res {
            Err(_) => None,
            Ok(res) => res.ok(),
        }
    }
}
