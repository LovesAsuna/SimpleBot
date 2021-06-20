package me.lovesasuna.bot.controller.photo.source

import me.lovesasuna.bot.controller.photo.MultiPhoto
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.network.OkHttpUtil
import java.io.IOException

/**
 * @author LovesAsuna
 */
class Pixiv : MultiPhoto {
    override fun fetchData(num: Int): List<String> {
        val source = "https://api.lolicon.app/setu/v2?num=${if (num > 10) 10 else num}"
        val result = OkHttpUtil.getIs(OkHttpUtil[source])
        val root = BotData.objectMapper.readTree(result)
        return ArrayList<String>().apply {
            try {
                repeat(num) {
                    val url = root["data"][it]["urls"]["original"]
                    this.add(url.asText())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }
}