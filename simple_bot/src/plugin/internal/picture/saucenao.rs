use crate::plugin::internal::picture::search_source::{PictureResult, SearchSource};
use lazy_static::lazy_static;
use proc_qq::re_exports::async_trait::async_trait;

pub struct SauceNao;

lazy_static! {
    static ref API: String = format!(
        "https://saucenao.com/search.php?db=999&output_type=2&testmode=1&api_key={}&numres=16&url=",
        crate::CONFIG.saucenao.api_key
    );
}

#[async_trait]
impl SearchSource for SauceNao {
    fn get_name(&self) -> &str {
        "SauceNao"
    }

    async fn search(
        &self,
        url: String,
    ) -> anyhow::Result<Vec<Box<dyn PictureResult + Send + Sync>>> {
        let mut result: Vec<Box<dyn PictureResult + Send + Sync>> = Vec::new();
        let resp = reqwest::get(API.clone() + &url).await?;
        let d = &resp.text().await?;
        let value = serde_json::from_str::<serde_json::Value>(d)?;
        let results = value["results"].as_array();
        if results.is_none() {
            return Ok(result);
        }
        let results = results.unwrap();
        for i in 0..results.len() {
            let res = &results[i];
            let similarity = res["header"]["similarity"].as_str();
            if similarity.is_none() {
                continue;
            }
            let similarity = similarity.unwrap().parse::<f64>().unwrap();
            if similarity < 57.5 {
                continue;
            }
            let mut ext_urls_list = Vec::new();
            match res["data"]["ext_urls"].as_array() {
                Some(ext_urls) => {
                    for url in ext_urls {
                        ext_urls_list
                            .push(url.as_str().map_or(String::default(), |s| s.to_string()));
                    }
                }
                None => {}
            }
            if !ext_urls_list.iter().any(|s| s.contains("pixiv")) {
                continue;
            }
            let thumbnail = res["header"]["thumbnail"].as_str().unwrap_or_default();
            let member_name = res["data"]["member_name"].as_str().unwrap_or_default();
            result.push(Box::new(SauceNaoResult {
                similarity,
                thumbnail: thumbnail.to_string(),
                ext_urls: ext_urls_list,
                member_name: member_name.to_string(),
            }));
        }
        Ok(result)
    }
}

struct SauceNaoResult {
    similarity: f64,
    thumbnail: String,
    ext_urls: Vec<String>,
    member_name: String,
}

impl PictureResult for SauceNaoResult {
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
