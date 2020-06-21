package me.lovesasuna.bot.function.colorphoto

import me.lovesasuna.bot.data.BotData.objectMapper
import me.lovesasuna.bot.util.NetWorkUtil.get
import java.io.IOException

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:06
 */
class Pixiv : Source {
    override fun fetchData(): String? {
        // 备用976835505edf70ff564238
        val source = "https://api.lolicon.app/setu/?apikey=560424975e992113ed5977"
        val result = get(source)
        return try {
            val inputStream = result!!.first
            val root = objectMapper!!.readTree(inputStream)
            root["data"][0]["url"].asText() + "|" + root["quota"].asText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
}