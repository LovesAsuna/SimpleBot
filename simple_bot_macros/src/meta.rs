use proc_macro::TokenStream;
use std::collections::HashMap;

use syn::parse::{Parse, ParseStream};

use crate::arg::Arg;

pub struct Meta {
    pub pattern: String,
    pub args: HashMap<usize, String>,
}

impl Parse for Meta {
    fn parse(input: ParseStream) -> syn::Result<Self> {
        let look_head = input.lookahead1();
        if look_head.peek(syn::LitStr) {
            let lit_str = input.parse::<syn::LitStr>().unwrap();
            let pattern = lit_str.value();
            let stream = pattern.parse::<TokenStream>().unwrap();
            let args = syn::parse::<Arg>(stream)
                .map(|args| {
                    args.clone()
                        .into_iter()
                        .enumerate()
                        .collect::<HashMap<_, _>>()
                })
                .ok()
                .unwrap_or(HashMap::new());
            Ok(Meta { pattern, args })
        } else {
            return Err(input.error("pattern must be a LitStr"));
        }
    }
}
