mod ascii2d;
mod pixiv_proxy;
mod saucenao;
mod search;
mod search_source;

pub use pixiv_proxy::PixivProxy;
use proc_qq::{module, Module};
pub use search::Search;

pub(super) fn module() -> Module {
    module!("picture", "图片")
}
