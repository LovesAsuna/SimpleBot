mod baidu;
mod guess_meaning;
mod jikipedia;
mod news;

pub use baidu::Baidu;
pub use guess_meaning::GuessMeaning;
pub use jikipedia::Jikipedia;
pub use news::News;
use proc_qq::{module, Module};

pub(super) fn module() -> Module {
    module!("infomation", "信息")
}
