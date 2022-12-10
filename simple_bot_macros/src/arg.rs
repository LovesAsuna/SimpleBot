use std::collections::HashSet;
use std::ops::Deref;

use syn::parse::{Parse, ParseStream};

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
        let input = input.parse::<proc_macro2::TokenStream>()?;
        Self::do_parse(&input)
    }
}

impl Arg {
    pub fn do_parse(token_stream: &proc_macro2::TokenStream) -> syn::Result<Self> {
        let buf = token_stream.clone().into_iter().collect::<Vec<_>>();
        let mut args = Vec::new();
        let mut args_set = HashSet::new();
        for i in 0..buf.len() {
            if let proc_macro2::TokenTree::Group(group) = &buf[i] {
                let buffer = syn::buffer::TokenBuffer::new2(group.stream());
                let cursor = buffer.begin();
                if let Some((ident, next)) = cursor.ident() {
                    if !args_set.insert(ident.to_string()) {
                        return Err(syn::Error::new(
                            cursor.span(),
                            format!(r#"duplicate placeholders found: "{}""#, ident),
                        ));
                    }

                    if !next.eof() {
                        return Err(syn::Error::new(
                            next.span(),
                            format!(r#"something else besides placeholder: "{}""#, ident),
                        ));
                    }
                    args.push(ident.to_string());
                }
            }
        }
        Ok(Arg(args))
    }
}
