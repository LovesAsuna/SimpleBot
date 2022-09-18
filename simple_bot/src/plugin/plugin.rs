use std::ops::Deref;

use lazy_static::lazy_static;
use proc_qq::{MessageChainPointTrait, MessageContentTrait, MessageEvent};
use proc_qq::re_exports::async_trait::async_trait;

pub trait Plugin {
    fn get_name(&self) -> &str;
    fn get_desc(&self) -> &str;
}

pub trait ActionSelector {
    fn select(&self, event: &MessageEvent) -> Option<Vec<String>>;
}

pub struct ActionFunc(pub Box<dyn Fn(&MessageEvent, Vec<String>) -> anyhow::Result<bool>>);

unsafe impl Sync for ActionFunc {}

unsafe impl Send for ActionFunc {}

pub struct Action {
    pub act: ActionFunc,
    pub pattern: String,
}

lazy_static! {
    static ref ARG_REGEX: regex::Regex = regex::Regex::new(r"\{\w+\}").unwrap();
}

impl ActionSelector for Action {
    fn select(&self, event: &MessageEvent) -> Option<Vec<String>> {
        let message_chain = event.message_chain();
        let message_content = message_chain.message_content();
        let pattern = ARG_REGEX.replace_all(&self.pattern, r"(.*)");
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

#[async_trait]
pub trait CommandPlugin: Plugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        for action in self.get_actions() {
            let select_res = action.select(event);
            if let Some(slot_content) = select_res {
                let _ = (action.act.0)(event, slot_content);
            }
        }
        Ok(true)
    }

    fn get_actions(&self) -> &Vec<Action>;
}

#[async_trait]
pub trait RawPlugin: Plugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool>;
}