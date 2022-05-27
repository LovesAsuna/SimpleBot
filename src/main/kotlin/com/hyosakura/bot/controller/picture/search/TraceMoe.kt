package com.hyosakura.bot.controller.picture.search

import com.fasterxml.jackson.databind.JsonNode
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.network.Request.toJson
import io.ktor.client.statement.*
import io.ktor.http.*
import java.math.RoundingMode
import java.text.DecimalFormat

object TraceMoe : SearchSource<AnimeResult> {
    override val name: String = "trace.moe"
    private val mapper = BotData.objectMapper
    private val queryString =
        "query (${"$"}ids: [Int]) {Page(page: 1, perPage: 50) {media(id_in: ${"$"}ids, type: ANIME) {id title{native}startDate{year month day}endDate{year month day}season episodes duration coverImage{large medium}bannerImage externalLinks{id url site}}}}"
    private val format = DecimalFormat().also {
        it.maximumFractionDigits = 2
        it.roundingMode = RoundingMode.FLOOR
    }

    override suspend fun search(url: String): List<AnimeResult> {
        val response = Request.submitFormWithBinaryData(
            "https://api.trace.moe/search",
            emptyMap(),
            Request.get(url).readBytes(),
            "image",
            {
                append(HttpHeaders.ContentDisposition, "filename=blob")
            }
        )
        var resultNode = response.toJson()["result"]
        var size = resultNode.size().let {
            if (it > 3) 3 else it
        }
        val body = mapper.createObjectNode()
        val array = mapper.createArrayNode()
        val results = mutableListOf<AnimeResult>()
        repeat(size) {
            val id = resultNode[it]["anilist"].asInt()
            array.add(id)
            val similarity = format.format(resultNode[it]["similarity"].asDouble() * 100)
            val from = getTime(resultNode[it]["from"].asDouble())
            val to = getTime(resultNode[it]["to"].asDouble())
            val result = AnimeResult()
            result.similarity = similarity.toDouble()
            result.from = from
            result.to = to
            results.add(result)
        }

        body.put("query", queryString)
            .set<JsonNode>(
                "variables", mapper.createObjectNode()
                    .set(
                        "ids", array
                    )
            )
        resultNode = Request.postJson(
            "https://trace.moe/anilist/",
            body
        ).toJson()["data"]["Page"]["media"]
        size = resultNode.size()
        repeat(size) {
            val index = size - 1 - it
            val currentResult = resultNode[index]
            val title = currentResult["title"].elements().let {
                mutableListOf<String>().apply {
                    while (it.hasNext()) {
                        add(it.next().asText())
                    }
                }
            }
            val startDate = currentResult["startDate"].let { js ->
                "${js["year"].asText()}-${js["month"].asText()}-${js["day"].asText()}"
            }
            val endDate = currentResult["endDate"].let { js ->
                "${js["year"].asText()}-${js["month"].asText()}-${js["day"].asText()}"
            }
            val season = currentResult["season"].asText()
            val episodes = currentResult["episodes"].asText()
            val duration = currentResult["duration"].asText()
            val cover = currentResult["coverImage"]["large"].asText()
            val banner = currentResult["bannerImage"].asText()
            val extUrls = mutableListOf<List<String>>().apply {
                val externalLinks = currentResult["externalLinks"]
                repeat(externalLinks.size()) { index ->
                    add(listOf(externalLinks[index]["site"].asText(), externalLinks[index]["url"].asText()))
                }
            }
            results[it].apply {
                this.title = title
                this.startDate = startDate
                this.endDate = endDate
                this.season = season
                this.episodes = episodes
                this.duration = duration
                this.cover = cover
                this.banner = banner
                this.extUrls = extUrls
            }
        }
        return results
    }

    private fun getTime(time: Double): String {
        return format.format((time / 60).toInt() + "0.${(time / 60).toString().substringAfter(".")}".toDouble() * 0.6)
    }
}