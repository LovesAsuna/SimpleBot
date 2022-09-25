use crate::plugin::{CommandPlugin, RawPlugin};

pub mod video;
pub mod picture;

pub fn register_command_plugins() -> Vec<Box<dyn CommandPlugin + Send + Sync>> {
    vec![
        Box::new(picture::Search::new())
    ]
}

pub fn register_raw_plugins() -> Vec<Box<dyn RawPlugin + Send + Sync>> {
    vec![
        Box::new(video::BilibiliVideo::new())
    ]
}