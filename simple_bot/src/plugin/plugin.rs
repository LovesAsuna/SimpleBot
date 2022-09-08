use proc_qq::MessageEvent;
use proc_qq::re_exports::async_trait::async_trait;

pub trait CommandPlugin {
    fn get_commands(&self) -> Vec<String>;
}

#[async_trait]
pub trait RawPlugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool>;
}