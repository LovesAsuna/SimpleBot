use proc_qq::Module;

mod chat;
mod information;
mod picture;
mod video;

pub(super) fn plugin_entry() -> Vec<Module> {
    vec![
        chat::module(),
        information::module(),
        picture::module(),
        video::module(),
    ]
}
