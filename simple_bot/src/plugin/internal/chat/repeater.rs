use crate::plugin::{Plugin, RawPlugin};
use async_trait::async_trait;
use proc_qq::{MessageChainPointTrait, MessageContentTrait, MessageEvent, MessageSendToSourceTrait};
use std::cell::RefCell;
use std::collections::VecDeque;
use rand::prelude::SliceRandom;

pub struct Repeater {
    stack: RefCell<VecDeque<String>>,
    capacity: usize
}

unsafe impl Sync for Repeater {}

impl Repeater {
    pub fn new(capacity: usize) -> Self {
        Repeater {
            stack: RefCell::new(VecDeque::with_capacity(capacity)),
            capacity
        }
    }
}

impl Plugin for Repeater {
    fn get_name(&self) -> &str {
        "复读机"
    }

    fn get_desc(&self) -> &str {
        "复读机"
    }
}

#[async_trait]
impl RawPlugin for Repeater {
    async fn on_event(&self, event: &MessageEvent) -> anyhow::Result<bool> {
        let message = event.message_chain();
        let content = message.message_content();
        {
            let stack = self.stack.borrow_mut();
            if stack.len() <= self.capacity {
                return Ok(false);
            }
        }
        let run = {
            let mut stack = self.stack.borrow_mut();
            stack.pop_front();
            stack.push_back(content);
            let iter = stack.iter();
            all_equal(iter)
        };
        if run {
            let mut message = message.clone();
            message.0.shuffle(&mut rand::thread_rng());
            event.send_message_to_source(message).await.unwrap();
            Ok(true)
        } else {
            Ok(false)
        }
    }
}

fn all_equal<'a, T: Iterator<Item = &'a String>>(mut iter: T) -> bool {
    match iter.next() {
        None => true,
        Some(a) => iter.all(|x| a == x),
    }
}
