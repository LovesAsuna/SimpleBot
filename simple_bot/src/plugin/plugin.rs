use proc_qq::MessageEvent;
use proc_qq::re_exports::async_trait::async_trait;

pub trait Plugin {
    fn get_name(&self) -> &str;
    fn get_desc(&self) -> &str;
}

pub trait CommandPlugin: Plugin {
    fn get_prefix(&self) -> String;
    fn get_commands(&self) -> Vec<String>;
}

#[async_trait]
pub trait RawPlugin: Plugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool>;
}