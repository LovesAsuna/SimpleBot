use crate::plugin::{CommandPlugin, RawPlugin};

mod chat;
mod help;
mod information;
mod picture;
mod video;

pub fn register_command_plugins() -> Vec<Box<dyn CommandPlugin + Send + Sync>> {
    vec![
        Box::new(picture::Search::new()),
        Box::new(picture::PixivProxy::new()),
        Box::new(information::Baidu::new()),
        Box::new(information::GuessMeaning::new()),
        Box::new(information::Jikipedia::new()),
        Box::new(chat::KeyWord::new()),
        Box::new(help::Help::new()),
        Box::new(chat::Remind::new()),
    ]
}

pub fn register_raw_plugins() -> Vec<Box<dyn RawPlugin + Send + Sync>> {
    vec![
        Box::new(video::BilibiliVideo::new()),
        Box::new(chat::KeyWord::new()),
        Box::new(chat::Repeater::new(3)),
    ]
}
