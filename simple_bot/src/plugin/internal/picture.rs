mod ascii2d;
mod pixiv_proxy;
mod saucenao;
mod search;
mod search_source;

use proc_qq::Module;

pub(super) fn module() -> Module {
    Module {
        id: "picture".to_owned(),
        name: "图片".to_owned(),
        handles: {
            let mut handlers = Vec::new();
            handlers.append(&mut pixiv_proxy::handlers());
            handlers.append(&mut search::handlers());
            handlers
        },
    }
}
