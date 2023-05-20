mod keyword;
mod remind;
mod repeater;

pub use keyword::KeyWord;
use proc_qq::Module;
pub use remind::Remind;
pub use repeater::Repeater;

pub(super) fn module() -> Module {
    Module {
        id: "chat".to_owned(),
        name: "聊天".to_owned(),
        handles: keyword::handlers() + repeater::handlers(),
    }
}
