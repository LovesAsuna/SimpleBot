mod keyword;
mod remind;
mod repeater;

pub use keyword::KeyWord;
use proc_qq::{module, Module};
pub use remind::Remind;
pub use repeater::Repeater;

pub(super) fn module() -> Module {
    module!("chat", "聊天")
}
