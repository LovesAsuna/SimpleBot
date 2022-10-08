use crate::plugin::{CommandPlugin, RawPlugin};

mod video;
mod picture;
mod information;
mod chat;
mod help;

pub fn register_command_plugins() -> Vec<Box<dyn CommandPlugin + Send + Sync>> {
    vec![
        Box::new(picture::Search::new()),
        Box::new(picture::PixivProxy::new()),
        Box::new(information::Baidu::new()),
        Box::new(information::GuessMeaning::new()),
        Box::new(information::Jikipedia::new()),
        Box::new(help::Help::new()),
    ]
}

pub fn register_raw_plugins() -> Vec<Box<dyn RawPlugin + Send + Sync>> {
    vec![
        Box::new(video::BilibiliVideo::new()),
        Box::new(chat::KeyWord::new())
    ]
}