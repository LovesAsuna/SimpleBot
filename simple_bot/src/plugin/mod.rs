use proc_qq::Module;

mod internal;

pub trait Plugin {
    fn get_name(&self) -> &str;
    fn get_desc(&self) -> &str;
}

pub(crate) fn plugin_entry() -> Vec<Module> {
    internal::plugin_entry()
}
