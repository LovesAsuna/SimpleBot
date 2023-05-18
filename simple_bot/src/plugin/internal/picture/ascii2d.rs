use crate::plugin::internal::picture::search_source::{PictureResult, SearchSource};
use async_trait::async_trait;

pub struct Ascii2d;

const API: &str = "https://ascii2d.net/search/url/";

#[async_trait]
impl SearchSource for Ascii2d {
    fn get_name(&self) -> &str {
        "Ascii2d"
    }

    async fn search(
        &self,
        url: String,
    ) -> anyhow::Result<Vec<Box<dyn PictureResult + Send + Sync>>> {
        let mut result: Vec<Box<dyn PictureResult + Send + Sync>> = Vec::new();
        let client = reqwest::ClientBuilder::new().build().unwrap();
        let resp = client
            .get(API.clone() + &url)
            .header("User-Agent", "PostmanRuntime/7.29.2")
            .send()
            .await?;
        let text = resp.text().await?;
        let dom = visdom::Vis::load(text).unwrap();
        let container = dom.find(".container");
        for i in 1..=2 {
            let item = container.find("div.row.item-box");
            let sources = item.get(i).unwrap();
            let thumbnail = format!(
                "https://ascii2d.net{}",
                sources
                    .children()
                    .find("img[loading=lazy]")
                    .attr("src")
                    .unwrap()
                    .to_string()
            );
            let mut ext_urls_list = Vec::new();
            for url in sources.children().find("a[target]") {
                ext_urls_list.push(url.get_attribute("href").unwrap().to_string());
            }
            result.push(Box::new(Ascii2dResult {
                similarity: -1.0,
                thumbnail,
                ext_urls: ext_urls_list,
                member_name: String::from("Ascii2d不显示"),
            }))
        }
        Ok(result)
    }
}

struct Ascii2dResult {
    similarity: f64,
    thumbnail: String,
    ext_urls: Vec<String>,
    member_name: String,
}

impl PictureResult for Ascii2dResult {
    fn get_similarity(&self) -> f64 {
        self.similarity
    }

    fn get_thumbnail(&self) -> String {
        self.thumbnail.to_string()
    }

    fn get_ext_urls(&self) -> &Vec<String> {
        &self.ext_urls
    }

    fn get_member_name(&self) -> String {
        self.member_name.to_string()
    }
}
