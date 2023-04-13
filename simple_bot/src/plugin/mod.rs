use proc_qq::Module;

mod internal;

pub(crate) fn plugin_entry() -> Vec<Module> {
    internal::plugin_entry()
}
