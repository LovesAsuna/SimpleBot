use proc_macro::TokenStream;
use std::collections::{HashMap, HashSet};
use std::ops::Deref;

use quote::{quote, ToTokens};
use syn::{braced, Ident, ItemFn, parse_macro_input};
use syn::parse::{Parse, ParseStream};

#[derive(Debug)]
struct Args(Vec<String>);

impl Deref for Args {
    type Target = Vec<String>;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl Parse for Args {
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
        Ok(Args(args))
    }
}

struct Meta {
    pattern: String,
    args: HashMap<usize, String>,
}

impl Parse for Meta {
    fn parse(input: ParseStream) -> syn::Result<Self> {
        let look_head = input.lookahead1();
        if look_head.peek(syn::LitStr) {
            let lit_str = input.parse::<syn::LitStr>().unwrap();
            let pattern = lit_str.value();
            let stream = pattern.parse::<TokenStream>().unwrap();
            let args = syn::parse::<Args>(stream).map(
                |args| {
                    args
                        .clone()
                        .into_iter()
                        .enumerate()
                        .collect::<HashMap<_, _>>()
                }
            ).ok().unwrap_or(HashMap::new());
            Ok(Meta {
                pattern,
                args,
            })
        } else {
            return Err(input.error("pattern must be a LitStr"));
        }
    }
}

#[proc_macro_attribute]
pub fn action(meta: TokenStream, input: TokenStream) -> TokenStream {
    let meta = parse_macro_input!(meta as Meta);
    let function = parse_macro_input!(input as ItemFn);
    let params = &function.sig.inputs;
    let function_name = &function.sig.ident;
    let mut param_name_and_type = Vec::new();
    for param in params {
        match param {
            syn::FnArg::Receiver(_) => continue,
            syn::FnArg::Typed(pt) => {
                let param_pat = pt.pat.as_ref();
                let param_ty = pt.ty.as_ref();
                let param_name = param_pat.to_token_stream().to_string();
                let param_type = match param_ty {
                    syn::Type::Path(tp) => {
                        let last_segment = tp.path.segments.last().unwrap();
                        let mut param_type = last_segment.ident.to_string();
                        if param_type != "Option" {
                            unimplemented!("type {} must wrapped in option", param_type);
                        }
                        param_type = match &last_segment.arguments {
                            syn::PathArguments::AngleBracketed(arg) => {
                                arg.args.last().unwrap().to_token_stream().to_string()
                            }
                            _ => {
                                unimplemented!();
                            }
                        };
                        param_type
                    }
                    _ => {
                        unimplemented!("not supported type")
                    }
                };
                param_name_and_type.push((param_name, param_type));
            }
        }
    }
    let mut invoke_builder = String::new();
    for i in 0..param_name_and_type.len() {
        let param_name: &String = &param_name_and_type[i].0;
        let param_type: &String = &param_name_and_type[i].1;
        match param_type.as_str() {
            "RQElem" => {
                invoke_builder.push_str(&format!(r#"element_map.remove(&"{}".to_string()),"#, param_name));
            }
            "String" => {
                invoke_builder.push_str(&format!(r#"text_map.remove(&"{}".to_string()),"#, param_name));
            }
            _ => {
                invoke_builder.push_str(&format!(r#"text_map.remove(&"{}".to_string()).and_then(|v| v.parse()),"#, param_name));
            }
        }
    }

    let invoke: syn::Stmt = syn::parse_str(
        &format!(
            r"Self::{}(
            {}
        );",
            function_name,
            invoke_builder
        )
    ).unwrap();
    let pattern = meta.pattern.to_token_stream();
    let dispatcher_function_name = Ident::new(&(function_name.to_string() + "_dispatcher"), function_name.span());
    let args_json = serde_json::to_string(&meta.args).unwrap();
    let param_name_and_type_json = serde_json::to_string(&param_name_and_type).unwrap();
    let closure = quote! {
        // closure will be invoked when action is selected
        Box::new(
            |event: &proc_qq::MessageEvent, slot_content: Vec<String>| -> anyhow::Result<bool> {
                let args_json = #args_json;
                let param_name_and_type_json = #param_name_and_type_json;
                let args = serde_json::from_str::<std::collections::HashMap<usize, String>>(&args_json).expect("args deserialize error");
                let param_name_and_type = serde_json::from_str::<Vec<(String, String)>>(&param_name_and_type_json).expect("param_name_and_type deserialize error");
                let message_chain = <proc_qq::MessageEvent as proc_qq::MessageChainPointTrait>::message_chain(event);
                let elements = message_chain.clone().into_iter().enumerate();
                let mut text_map = std::collections::HashMap::new();
                let mut element_map = std::collections::HashMap::new();
                for (index, element) in elements {
                    // found a RQElem
                    let arg_name = args.get(&index);
                    if let Some(arg_name) = arg_name {
                        if element.to_string() == slot_content[index] {
                            element_map.insert(arg_name.clone(), element);
                        } else {
                            text_map.insert(arg_name.clone(), slot_content[index].clone());
                        }
                    }
                }
                #invoke
                return Ok(true);
            }
        )
    };
    let dispatcher_function = quote! {
        fn #dispatcher_function_name() -> crate::plugin::Action {
            crate::plugin::Action {
                act: crate::plugin::ActionFunc(#closure),
                pattern: #pattern.to_string()
            }
        }
    };
    let gen_code = quote! {
        #function
        #dispatcher_function
    };
    TokenStream::from(gen_code)
}
