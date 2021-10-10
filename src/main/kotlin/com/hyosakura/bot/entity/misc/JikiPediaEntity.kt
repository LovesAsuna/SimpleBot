package com.hyosakura.bot.entity.misc

import com.fasterxml.jackson.databind.JsonNode

/**
 * @author LovesAsuna
 **/
data class JikiPediaEntity(val title : String, val tags : List<String>, val content : String) {
    companion object {
        fun parse(json: JsonNode): JikiPediaEntity? {
            val category = json["category"].asText()
            if (category != "definition") return null
            val definitions = json["definitions"][0]
            val title = definitions["term"]["title"].asText()
            val tags = mutableListOf<String>()
            definitions["tags"].forEach {
                tags.add(it["name"].asText())
            }
            val content = definitions["plaintext"].asText()
            return JikiPediaEntity(title, tags, content)
        }
    }

    override fun toString(): String = "定义: [$title] 标签: $tags\n$content"
}