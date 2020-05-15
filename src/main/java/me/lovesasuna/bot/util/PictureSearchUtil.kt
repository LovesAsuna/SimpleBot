package me.lovesasuna.bot.util

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.file.Config

object PictureSearchUtil {
    private val api = "https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=16&api_key=${Config.config.getString("PictureSearchAPI")}&url="
    private val mapper = ObjectMapper()

    fun search(url: String): List<Result> {
        val inputStream = NetWorkUtil.fetch(api + url)?.first ?: return emptyList()
        val results = mapper.readTree(inputStream)["results"]
        val resultList = ArrayList<Result>()
        repeat(3) { i ->
            val result = results[i]
            if (result != null) {
                val similarity = result["header"]["similarity"].asInt()
                val thumbnail = result["header"]["thumbnail"].asText()
                val extUrlsList = ArrayList<String>()
                repeat(result["data"]["ext_urls"].size()) {
                    extUrlsList.add(result["data"]["ext_urls"][it].asText())
                }
                resultList.add(Result(similarity, thumbnail, extUrlsList))
            }
        }

        return resultList
    }

    data class Result(val similarity: Int, val thumbnail: String, val extUrls: List<String>)
}