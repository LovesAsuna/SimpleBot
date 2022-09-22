use proc_qq::*;

use crate::plugin;
use crate::future::SESSION_CACHE;

macro_rules! on_event {
    ($plugins:expr, $event:expr) => {
        for plugin in $plugins {
            let _ = plugin.on_event($event).await;
        }
    }
}

#[event]
pub async fn message_handler(event: &MessageEvent) -> anyhow::Result<bool> {
    let message_chain = event.message_chain();
    {
        let mut cache = SESSION_CACHE.lock().await;
        if let Some(tx)  = cache.remove(&event.from_uin()) {
            tx.send(message_chain.clone()).unwrap();
        }
    }
    on_event!(plugin::RAW_PLUGINS.as_ref(), event);
    on_event!(plugin::COMMAND_PLUGINS.as_ref(), event);
    Ok(true)
}
