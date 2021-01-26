package me.lovesasuna.bot.controller.photo.source

import me.lovesasuna.bot.data.BotData.objectMapper
import me.lovesasuna.bot.data.pushError
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.IOException

/**
 * @author LovesAsuna
 */
class Pixiv : PhotoSource {
    override fun fetchData(): String? {
        //todo config
        val source = "https://api.lolicon.app/setu/?apikey=${""}"
        val result = NetWorkUtil[source]
        return try {
            val inputStream = result!!.second
            val root = objectMapper.readTree(inputStream)
            val quota = root["quota"].asText()
            val url = root["data"][0]?.let { it["url"].asText() } ?: return "|0"
            return "$url|$quota"
        } catch (e: IOException) {
            e.pushError()
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.pushError()
            e.printStackTrace()
            null
        }
    }
}