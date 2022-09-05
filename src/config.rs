use std::fs::File;

#[derive(serde::Serialize, serde::Deserialize)]
pub struct AccountConfig {
    pub account: i64,
    pub password: String,
    pub protocol: String
}

impl Default for AccountConfig {
    fn default() -> Self {
        AccountConfig {
            account: 0,
            password: String::default(),
            protocol: "IPAD".to_string()
        }
    }
}

pub fn read_config() -> anyhow::Result<AccountConfig> {
    let file_name = "config.yml";
    match File::open(file_name) {
        Ok(file) => {
            let config = serde_yaml::from_reader(file)?;
            Ok(config)
        },
        Err(_) => {
            let file = File::create(file_name)?;
            let default_config = AccountConfig::default();
            serde_yaml::to_writer(file, &default_config).unwrap();
            Err(anyhow::Error::msg("配置文件不存在，自动创建默认配置，请更改配置后重启机器人！"))
        }
    }
}