use std::sync::Arc;

use lazy_static::lazy_static;

pub use plugin::{CommandPlugin, RawPlugin};

mod plugin;
mod internal;

lazy_static! {
    static ref COMMAND_PLUGINS: Arc<Vec<Box<dyn CommandPlugin + Send + Sync>>> = Arc::new(
        internal::register_command_plugins()
    );
}

lazy_static! {
    pub static ref RAW_PLUGINS: Arc<Vec<Box<dyn RawPlugin + Send + Sync>>> = Arc::new(
        internal::register_raw_plugins()
    );
}





