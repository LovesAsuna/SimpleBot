package com.hyosakura.bot.controller.picture.search

import com.fasterxml.jackson.databind.JsonNode
import com.hyosakura.bot.util.network.OkHttpUtil
import com.hyosakura.bot.util.network.OkHttpUtil.mapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.RoundingMode
import java.text.DecimalFormat

object TraceMoe : SearchSource<AnimeResult> {
    private val client = OkHttpClient()
    private val queryString =
        "query (${"$"}ids: [Int]) {Page(page: 1, perPage: 50) {media(id_in: ${"$"}ids, type: ANIME) {id title{native}startDate{year month day}endDate{year month day}season episodes duration coverImage{large medium}bannerImage externalLinks{id url site}}}}"
    private val format = DecimalFormat().also {
        it.maximumFractionDigits = 2
        it.roundingMode = RoundingMode.FLOOR
    }

    override fun search(url: String): List<AnimeResult> {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "blob",
                OkHttpUtil.downloadBytes(url)
                    .let {
                        it.toRequestBody("multipart/form-data".toMediaType(), 0, it.size)
                    }
            )
            .build()

        val request = Request.Builder()
            .url("https://api.trace.moe/search")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        var resultNode = OkHttpUtil.getJson(response)["result"]
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
        resultNode = OkHttpUtil.postJson(
            "https://trace.moe/anilist/",
            body.toString().toRequestBody("application/json".toMediaType())
        )["data"]["Page"]["media"]
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