use std::collections::HashMap;

use syn::parse::{Parse, ParseStream};

use crate::arg::Arg;

pub struct Meta {
    pub pattern: String,
    pub args: HashMap<usize, String>,
}

impl Parse for Meta {
    fn parse(input: ParseStream) -> syn::Result<Self> {
        let lit_str = input.parse::<syn::LitStr>()?;
        let pattern = lit_str.value();
        let stream = pattern
            .parse::<proc_macro2::TokenStream>()
            .map_err(|e| syn::Error::new(lit_str.span(), e.to_string()))?;
        let args = syn::parse2::<Arg>(stream).map(|args| {
            args.clone()
                .into_iter()
                .enumerate()
                .collect::<HashMap<_, _>>()
        })?;
        Ok(Meta { pattern, args })
    }
}
