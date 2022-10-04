use rbatis::crud;
use serde::{Serialize, Deserialize};

#[derive(Clone, Serialize, Deserialize)]
pub struct KeyWord {
    pub group_id: i64,
    pub regex: Option<String>,
    pub reply: Option<String>,
    pub chance: i32
}

crud!(KeyWord{});