package me.lovesasuna.bot.entity.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class PixivCatEntity(

	@field:JsonProperty("original_urls")
	val originalUrls: List<String?>? = null,

	@field:JsonProperty("success")
	val success: Boolean? = null,

	@field:JsonProperty("id_str")
	val idStr: String? = null,

	@field:JsonProperty("original_urls_proxy")
	val originalUrlsProxy: List<String?>? = null,

	@field:JsonProperty("multiple")
	val multiple: Boolean? = null,

	@field:JsonProperty("html")
	val html: String? = null,

	@field:JsonProperty("id")
	val id: Int? = null
)
