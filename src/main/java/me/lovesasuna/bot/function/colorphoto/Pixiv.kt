package me.lovesasuna.bot.function.colorphoto

import me.lovesasuna.bot.data.BotData.objectMapper
import me.lovesasuna.bot.data.pushError
import me.lovesasuna.bot.util.interfaces.PhotoSource
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.IOException

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:06
 */
class Pixiv : PhotoSource {
    override fun fetchData(): String? {
        // 备用976835505edf70ff564238
        val source = "https://api.lolicon.app/setu/?apikey=560424975e992113ed5977"
        val result = NetWorkUtil[source]
        return try {
            val inputStream = result!!.second
            val root = objectMapper!!.readTree(inputStream)
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