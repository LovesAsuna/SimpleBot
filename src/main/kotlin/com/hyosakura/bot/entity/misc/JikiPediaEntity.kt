package com.hyosakura.bot.entity.misc

import com.fasterxml.jackson.databind.JsonNode

/**
 * @author LovesAsuna
 **/
data class JikiPediaEntity(val title: String, val content: String) {
    companion object {
        fun parse(json: JsonNode): JikiPediaEntity? {
            val entity = json["entities"][0] ?: return null
            if (entity["entity_category"].asText() != "definition") return null
            val title = entity["title"].asText()
            val content = entity["content"].asText()
            return JikiPediaEntity(title, content)
        }
    }

    override fun toString(): String = "定义: [$title]\n$content"
}