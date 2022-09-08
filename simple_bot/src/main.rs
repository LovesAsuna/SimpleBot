pub use proc_qq::*;
use proc_qq::re_exports::*;
use tracing::*;
use tracing_subscriber::layer::SubscriberExt;
use tracing_subscriber::util::SubscriberInitExt;

use handler::message_handler;

mod handler;
mod config;
mod plugin;

#[tokio::main]
async fn main() {
    init_logger();
    let res = config::read_config();
    let config = match res {
        Ok(config) => config,
        Err(e) => {
            warn!("{:?}", e);
            return;
        }
    };
    let builder = ClientBuilder::new();
    let client = builder
        .authentication(Authentication::UinPassword(config.account, config.password))
        .version(parse_protocol(config.protocol))
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