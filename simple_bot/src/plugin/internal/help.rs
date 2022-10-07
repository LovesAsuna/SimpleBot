use proc_qq::{MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait};
use simple_bot_macros::{action, make_action};
use crate::plugin::{Action, CommandPlugin, Plugin, RAW_PLUGINS};

pub struct Help {
    actions: Vec<Box<dyn Action>>
}

impl Help {
    pub fn new() -> Self {
        Help {
            actions: vec![
                make_action()
            ]
        }
    }
}

impl Plugin for Help {
    fn get_name(&self) -> &str {
        ""
    }

    fn get_desc(&self) -> &str {
        ""
    }
}

impl CommandPlugin for Help {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("/help")]
async fn help(event: &MessageEvent) {
    let mut raw_help = String::new();
    for plugin in crate::plugin::RAW_PLUGINS.as_ref() {
        let name = plugin.get_name();
        let desc = plugin.get_desc();
        raw_help.push_str(&format!("{}: {}\n", name, desc));
    }
    event.send_message_to_source(raw_help.parse_message_chain()).await.unwrap();
    let mut command_help = String::new();
    for plugin in crate::plugin::COMMAND_PLUGINS.as_ref() {
        let name = plugin.get_name();
        let desc = plugin.get_desc();
        for action in plugin.get_actions() {
            command_help.push_str(&format!("{}\n", action.get_pattern()));
        }
        command_help.push_str(&format!("{}: {}\n", name, desc));
    }
    event.send_message_to_source(command_help.parse_message_chain()).await.unwrap();
}