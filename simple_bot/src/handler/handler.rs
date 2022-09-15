use proc_qq::*;

use crate::plugin;
use crate::future::SESSION_CACHE;

#[event]
pub async fn message_handler(event: &MessageEvent) -> anyhow::Result<bool> {
    let message_chain = event.message_chain();
    {
        let mut cache = SESSION_CACHE.lock().await;
        if let Some(tx)  = cache.remove(&event.from_uin()) {
            tx.send(message_chain.clone()).unwrap();
        }
    }
    for plugin in plugin::RAW_PLUGINS.as_ref() {
        let _ = plugin.on_event(event).await;
    }
    Ok(true)
}
