mod bilibili;

use proc_qq::Module;

pub(super) fn module() -> Module {
    Module {
        id: "video".to_owned(),
        name: "视频".to_owned(),
        handles: {
            let mut handlers = Vec::new();
            handlers.append(&mut bilibili::handlers());
            handlers
        },
    }
}
