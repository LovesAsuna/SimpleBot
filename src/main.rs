mod handler;

pub use proc_qq::*;
use proc_qq::re_exports::*;
use proc_qq::re_exports::ricq::version::IPAD;
use tracing::*;
use tracing_subscriber::layer::SubscriberExt;
use tracing_subscriber::util::SubscriberInitExt;

#[tokio::main]
async fn main() {
    init_logger();
    let builder = ClientBuilder::new();
    let client = builder
        .authentication(Authentication::QRCode)
        .version(&IPAD)
        .show_slider_pop_menu_if_possible()
        .modules(register_module())
        .show_rq(Some(ShowQR::OpenBySystem))
        .build()
        .await
        .unwrap();
    client.start().await.unwrap().expect("启动时出现错误");
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
        )
        .init();
}

fn register_module() -> Vec<Module> {
    let mut modules = Vec::new();
    modules
}