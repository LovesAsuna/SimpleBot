use lazy_static::lazy_static;
pub use proc_qq::*;
use proc_qq::re_exports::*;
use tracing::*;
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

use handler::message_handler;

use crate::config::Config;

mod handler;
mod config;
mod plugin;
mod future;

lazy_static! {
    static ref CONFIG: Config = config::read_config().unwrap();
}

#[tokio::main]
async fn main() {
    init_logger();
    let builder = ClientBuilder::new();
    let client = builder
        .authentication(Authentication::UinPassword(CONFIG.account.account, CONFIG.account.password.clone()))
        .version(parse_protocol(CONFIG.account.protocol.clone()))
        .show_slider_pop_menu_if_possible()
        .modules(vec![module!("simple_bot", "handler", message_handler)])
        .show_rq(Some(ShowQR::OpenBySystem))
        .build()
        .await
        .unwrap();
    client.start().await.unwrap().expect("启动时出现错误");
}

fn parse_protocol(protocol: String) -> &'static ricq::version::Version {
    match protocol.as_ref() {
        "ANDROID_PHONE" => &ricq::version::ANDROID_PHONE,
        "ANDROID_WATCH" => &ricq::version::ANDROID_WATCH,
        "MACOS" => &ricq::version::MACOS,
        "QIDIAN" => &ricq::version::QIDIAN,
        _ => &ricq::version::IPAD
    }
}

fn init_logger() {
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::fmt::layer()
                .with_target(true)
                .without_time(),
        )
        .with(
            tracing_subscriber::filter::Targets::new()
                .with_target("ricq", Level::DEBUG)
                .with_target("proc_qq", Level::DEBUG)
                .with_target("simple_bot", Level::DEBUG)
        )
        .init();
}