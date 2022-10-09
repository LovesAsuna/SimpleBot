use std::collections::HashSet;
use std::ops::Deref;

use syn::parse::{Parse, ParseStream};
use syn::{braced, Ident};

#[derive(Debug)]
pub struct Arg(Vec<String>);

impl Deref for Arg {
    type Target = Vec<String>;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl Parse for Arg {
    fn parse(input: ParseStream) -> syn::Result<Self> {
        let mut args = Vec::new();
        let mut args_set = HashSet::new();
        while !input.is_empty() {
            let look_head = input.lookahead1();
            if look_head.peek(<Ident as syn::ext::IdentExt>::peek_any) {
                input.parse::<Ident>().unwrap();
                continue;
            }
            let content;
            braced!(content in input);
            if !content.is_empty() {
                let look_head = content.lookahead1();
                if look_head.peek(<Ident as syn::ext::IdentExt>::peek_any) {
                    let ident = content.parse::<Ident>().unwrap();
                    if !args_set.insert(ident.to_string()) {
                        return Err(content.error("duplicate placeholders found"));
                    }
                    args.push(ident.to_string());
                } else {
                    return Err(content.error("not an ident"));
                }
            }
        }
        Ok(Arg(args))
    }
}
