use proc_qq::Module;

pub use baidu::Baidu;
pub use guess_meaning::GuessMeaning;
pub use jikipedia::Jikipedia;
pub use news::News;

mod baidu;
mod guess_meaning;
mod jikipedia;
mod news;

pub(super) fn module() -> Module {
    Module {
        id: "infomation".to_owned(),
        name: "信息".to_owned(),
        handles: {
            let mut handlers = Vec::new();
            handlers.append(&mut baidu::handlers());
            handlers.append(&mut guess_meaning::handlers());
            handlers.append(&mut jikipedia::handlers());
            handlers.append(&mut news::handlers());
            handlers
        },
    }
}
