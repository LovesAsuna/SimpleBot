mod group_message_handler;

use proc_qq::Module;

pub fn register_module() -> Vec<Module> {
    let mut modules = Vec::new();
    modules.push(group_message_handler::register_module());
    modules
}