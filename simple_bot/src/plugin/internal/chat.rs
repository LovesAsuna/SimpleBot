use proc_qq::Module;

pub use keyword::KeyWord;
pub use remind::Remind;
pub use repeater::Repeater;

mod keyword;
mod remind;
mod repeater;

pub(super) fn module() -> Module {
    Module {
        id: "chat".to_owned(),
        name: "聊天".to_owned(),
        handles: {
            let mut handlers = Vec::new();
            handlers.append(&mut keyword::handlers());
            handlers.append(&mut remind::handlers());
            handlers.append(&mut repeater::handlers());
            handlers
        },
    }
}
