use crate::plugin::plugin::{CommandPlugin, RawPlugin};

pub fn register_command_plugins() -> Vec<Box<dyn CommandPlugin + Send + Sync>> {
    vec![]
}

pub fn register_raw_plugins() -> Vec<Box<dyn RawPlugin + Send + Sync>> {
    vec![]
}