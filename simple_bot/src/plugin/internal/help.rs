use crate::plugin::{Action, CommandPlugin, Plugin};
use proc_qq::{module, MessageChainParseTrait, MessageEvent, MessageSendToSourceTrait, Module};
use simple_bot_macros::{action, make_action};

pub struct Help {
    actions: Vec<Box<dyn Action>>,
}

impl Help {
    pub fn new() -> Self {
        Help {
            actions: vec![make_action!(help)],
        }
    }
}

impl Plugin for Help {
    fn get_name(&self) -> &str {
        "帮助"
    }

    fn get_desc(&self) -> &str {
        "查看所有命令帮助"
    }
}

impl CommandPlugin for Help {
    fn get_actions(&self) -> &Vec<Box<dyn Action>> {
        &self.actions
    }
}

#[action("/help")]
async fn help(event: &MessageEvent) -> anyhow::Result<bool> {
    let mut raw_help = String::from(format!("{:=^30}\n", "RAW"));
    let mut id = 1;
    for plugin in crate::plugin::RAW_PLUGINS.as_ref() {
        let name = plugin.get_name();
        let desc = plugin.get_desc();
        raw_help.push_str(&format!("{}. {}: {}\n", id, name, desc));
        id += 1;
    }
    event
        .send_message_to_source(raw_help.parse_message_chain())
        .await
        .unwrap();
    let mut command_help = String::from(format!("{:=^30}\n", "COMMAND"));
    let mut id = 1;
    for plugin in crate::plugin::COMMAND_PLUGINS.as_ref() {
        let name = plugin.get_name();
        let desc = plugin.get_desc();
        command_help.push_str(&format!("{}. {}: {}\n", id, name, desc));
        id += 1;
        for action in plugin.get_actions() {
            command_help.push_str(&format!("{}\n", action.get_pattern()));
        }
    }
    event
        .send_message_to_source(command_help.parse_message_chain())
        .await
        .unwrap();
    Ok(true)
}

pub(super) fn module() -> Module {
    module!("help", "帮助")
}
