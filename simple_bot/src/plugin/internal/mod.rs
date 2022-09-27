use crate::plugin::{CommandPlugin, RawPlugin};

mod video;
mod picture;
mod information;

pub fn register_command_plugins() -> Vec<Box<dyn CommandPlugin + Send + Sync>> {
    vec![
        Box::new(picture::Search::new()),
        Box::new(information::Baidu::new())
    ]
}

pub fn register_raw_plugins() -> Vec<Box<dyn RawPlugin + Send + Sync>> {
    vec![
        Box::new(video::BilibiliVideo::new())
    ]
}