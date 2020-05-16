package me.lovesasuna.bot.util

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.file.Config
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object PictureSearchUtil {
    private val api = "https://saucenao.com/search.php?db=999&output_type=2&testmode=1&numres=16&api_key=${Config.config.getString("PictureSearchAPI")}&url="
    private val mapper = ObjectMapper()

    fun search(url: String): List<Result> {
        val inputStream = NetWorkUtil.fetch(api + url)?.first ?: return emptyList()
        val results = mapper.readTree(inputStream)["results"]
        val resultList = ArrayList<Result>()
        for (i in 0..results.size()) {
            val result = results[i]
            if (result != null) {
                val similarity = result["header"]["similarity"].asInt()
                if (similarity < 57.5) continue
                val extUrlsList = ArrayList<String>()
                repeat(result["data"]["ext_urls"].size()) {
                    extUrlsList.add(result["data"]["ext_urls"][it].asText())
                }
                if (!extUrlsList.parallelStream().anyMatch { it.contains("pixiv") }) continue
                val thumbnail = result["header"]["thumbnail"].asText()
                resultList.add(Result(similarity, thumbnail, extUrlsList))
            }
        }

        return resultList
    }

    data class Result(val similarity: Int, val thumbnail: String, val extUrls: List<String>)
}