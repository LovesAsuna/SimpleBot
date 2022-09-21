use proc_macro::TokenStream;

use quote::{quote, ToTokens};
use syn::{Ident, ItemFn, parse_macro_input};

use meta::Meta;

mod meta;
mod arg;

#[proc_macro_attribute]
pub fn action(meta: TokenStream, input: TokenStream) -> TokenStream {
    let meta = parse_macro_input!(meta as Meta);
    let function = parse_macro_input!(input as ItemFn);
    let params = &function.sig.inputs;
    let function_name = &function.sig.ident;
    let mut param_infos = Vec::new();
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
                param_infos.push((param_name, param_type));
            }
        }
    }
    let mut invoke_builder = String::new();
    for i in 0..param_infos.len() {
        let param_name: &String = &param_infos[i].0;
        let param_type: &String = &param_infos[i].1;
        match param_type.as_str() {
            "RQElem" => {
                invoke_builder.push_str(&format!(r#"element_map.remove(&"{}".to_string()),"#, param_name));
            }
            "String" => {
                invoke_builder.push_str(&format!(r#"text_map.remove(&"{}".to_string()),"#, param_name));
            }
            _ => {
                invoke_builder.push_str(&format!(r#"text_map.remove(&"{}".to_string()).and_then(|v| v.parse().ok()),"#, param_name));
            }
        }
    }

    let invoke: syn::Stmt = syn::parse_str(
        &format!(r"Self::{}({});", function_name, invoke_builder)
    ).unwrap();
    let pattern = meta.pattern.to_token_stream();
    let args_json = serde_json::to_string(&meta.args).unwrap();
    let param_name_and_type_json = serde_json::to_string(&param_infos).unwrap();
    let closure = quote! {
        // closure will be invoked when action is selected
        Box::new(
            |event: &proc_qq::MessageEvent, slot_content: Vec<String>| -> anyhow::Result<bool> {
                let args_json = #args_json;
                let param_name_and_type_json = #param_name_and_type_json;
                let args = serde_json::from_str::<std::collections::HashMap<usize, String>>(&args_json).expect("args deserialize error");
                let param_name_and_type = serde_json::from_str::<Vec<(String, String)>>(&param_name_and_type_json).expect("param_name_and_type deserialize error");
                if param_name_and_type.len() != slot_content.len() {
                    return Ok(false);
                }
                let message_chain = <proc_qq::MessageEvent as proc_qq::MessageChainPointTrait>::message_chain(event);
                let elements = message_chain.clone().into_iter().filter(
                    |e| {
                        match e{
                            proc_qq::re_exports::ricq_core::msg::elem::RQElem::Other(_) => false,
                            _ => true
                        }
                    }
                ).collect::<Vec<proc_qq::re_exports::ricq_core::msg::elem::RQElem>>();
                let mut text_map = std::collections::HashMap::new();
                let mut element_map = std::collections::HashMap::new();
                // point to slot_content / param_name_and_type
                let mut i = 0;
                // point to message_chain elements
                let mut j = 0;
                while i < slot_content.len() {
                    match elements[j] {
                        proc_qq::re_exports::ricq_core::msg::elem::RQElem::Text(_) => {
                            while i < slot_content.len() && slot_content[i] != "" {
                                let need_name = &param_name_and_type[i].0;
                                let need_type = &param_name_and_type[i].1;
                                text_map.insert(need_name.clone(), slot_content[i].clone());
                                i += 1;
                            }
                            j += 1;
                        },
                        _ => {
                            let need_name = &param_name_and_type[i].0;
                            let need_type = &param_name_and_type[i].1;
                            element_map.insert(need_name.clone(), elements[i].clone());
                            i += 1;
                            j += 1;
                        }
                    }
                }
                #invoke
                return Ok(true);
            }
        )
    };
    let dispatcher_function_name = Ident::new(&(function_name.to_string() + "_dispatcher"), function_name.span());
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
