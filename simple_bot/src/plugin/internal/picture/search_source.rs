use proc_qq::re_exports::async_trait::async_trait;

#[async_trait]
pub trait SearchSource: Send + Sync {
    fn get_name(&self) -> &str;

    async fn search(&self, url: String) -> anyhow::Result<Vec<Box<dyn PictureResult + Send>>>;
}

pub trait PictureResult {
    fn get_similarity(&self) -> f64 {
        -1.0
    }

    fn get_thumbnail(&self) -> String;

    fn get_ext_urls(&self) -> &Vec<String>;

    fn get_member_name(&self) -> String;
}