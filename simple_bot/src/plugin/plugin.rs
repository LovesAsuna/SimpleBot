use std::ops::Deref;

use proc_qq::{MessageChainPointTrait, MessageContentTrait, MessageEvent};
use proc_qq::re_exports::async_trait::async_trait;
use proc_qq::re_exports::ricq_core::msg::elem::RQElem;
use simple_bot_macros::Action;

pub trait Plugin {
    fn get_name(&self) -> &str;
    fn get_desc(&self) -> &str;
}

pub trait ActionSelector {
    fn select(&self, event: &MessageEvent) -> bool;
}

type ActionFunc = dyn FnOnce(&MessageEvent) -> anyhow::Result<bool>;

pub struct Action {
    pub act: Box<ActionFunc>,
    pub pattern: String,
}

const ARG_REGEX: regex::Regex = regex::Regex::new(r"{\w+}").unwrap();

impl ActionSelector for Action {
    fn select(&self, event: &MessageEvent) -> bool {
        let message_chain = event.message_chain();
        let elements = message_chain.clone().into_iter();
        // the text contains only plain text
        let mut plain_text = String::new();
        for element in elements {
            if let RQElem::Text(text) = element {
                plain_text.push_str(&text.content);
            }
        }
        let pattern = ARG_REGEX.replace_all(&self.pattern, r"(\w*)");
        let regex = regex::Regex::new(pattern.deref());
        if let Ok(regex)  = regex {
            regex.is_match(&plain_text)
        } else {
            false
        }
    }
}

#[async_trait]
pub trait CommandPlugin: RawPlugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        let content = event.message_content();
        for action in self.get_actions() {
            if action.select(event) {
                action.act(event);
            }
        }
        Ok(true)

    }
    fn get_actions(&self) -> Vec<Action>;
}

#[async_trait]
pub trait RawPlugin: Plugin {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool>;
}