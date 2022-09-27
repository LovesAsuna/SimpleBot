use std::ops::Deref;
use std::sync::Arc;

use lazy_static::lazy_static;
use proc_qq::{MessageChainPointTrait, MessageEvent};
use proc_qq::re_exports::async_trait::async_trait;

mod internal;

lazy_static! {
    pub static ref COMMAND_PLUGINS: Arc<Vec<Box<dyn CommandPlugin + Send + Sync>>> = Arc::new(
        internal::register_command_plugins()
    );
}

lazy_static! {
    pub static ref RAW_PLUGINS: Arc<Vec<Box<dyn RawPlugin + Send + Sync>>> = Arc::new(
        internal::register_raw_plugins()
    );
}

pub trait Plugin {
    fn get_name(&self) -> &str;
    fn get_desc(&self) -> &str;
}

pub trait ActionSelector {
    fn select(&self, event: &MessageEvent) -> Option<Vec<String>>;
}

#[async_trait]
pub trait Action: Send + Sync {
    async fn do_action(&self, event: &MessageEvent, slot_content: Vec<String>) -> anyhow::Result<bool>;

    fn get_pattern(&self) -> String;
}

impl<T: Action + ?Sized> ActionSelector for T {
    fn select(&self, event: &MessageEvent) -> Option<Vec<String>> {
        let message_chain = event.message_chain();
        let mut message_content = String::new();
        let elements = message_chain.clone().into_iter().filter(
            |e| {
                match e {
                    proc_qq::re_exports::ricq_core::msg::elem::RQElem::Other(_) => false,
                    _ => true
                }
            }
        );
        for element in elements {
            match element {
                proc_qq::re_exports::ricq_core::msg::elem::RQElem::Text(text) => {
                    message_content.push_str(&text.content);
                }
                proc_qq::re_exports::ricq_core::msg::elem::RQElem::Other(_) => {}
                _ => {
                    message_content.push_str("");
                }
            }
        }
        let pattern = &self.get_pattern();
        let pattern = ARG_REGEX.replace_all(pattern, r"(.*)");
        let regex = regex::Regex::new(pattern.deref()).ok()?;
        let captures = regex.captures(&message_content);
        captures.map(
            |captures| {
                captures
                    .iter()
                    .into_iter()
                    .skip(1)
                    .filter(
                        |o| {
                            o.is_some()
                        }
                    )
                    .map(
                        |cap| {
                            cap.unwrap().as_str().to_string()
                        }
                    )
                    .collect::<Vec<String>>()
            }
        )
    }
}

lazy_static! {
    static ref ARG_REGEX: regex::Regex = regex::Regex::new(r"\{\w+\}").unwrap();
}

#[async_trait]
pub trait CommandPlugin: Plugin + Send + Sync {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        for action in self.get_actions() {
            let select_res = action.select(event);
            if let Some(slot_content) = select_res {
                let _ = action.do_action(event, slot_content).await;
            }
        }
        Ok(true)
    }

    fn get_actions(&self) -> &Vec<Box<dyn Action>>;
}

#[async_trait]
pub trait RawPlugin: Plugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool>;
}