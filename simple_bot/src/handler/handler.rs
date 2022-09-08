use proc_qq::*;

use crate::plugin;

#[event]
pub async fn message_handler(event: &MessageEvent) -> anyhow::Result<bool> {
    for plugin in plugin::RAW_PLUGINS.as_ref() {
        let _ = plugin.on_event(event).await;
    }
    Ok(true)
}
