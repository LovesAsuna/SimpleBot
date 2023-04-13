mod bilibili;

pub use bilibili::BilibiliVideo;
use proc_qq::{module, Module};

pub(super) fn module() -> Module {
    module!("video", "视频")
}
