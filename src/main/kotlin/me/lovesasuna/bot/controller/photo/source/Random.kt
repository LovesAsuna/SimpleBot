package me.lovesasuna.bot.controller.photo.source

import me.lovesasuna.bot.data.BotData.objectMapper
import me.lovesasuna.bot.data.pushError
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.IOException

/**
 * @author LovesAsuna
 */
class Random : PhotoSource {
    override fun fetchData(): String? {
        val source = "http://api.mtyqx.cn/api/random.php?return=json"
        val result = NetWorkUtil[source]
        return try {
            val root = objectMapper.readTree(result!!.second)
            root["imgurl"].asText()
        } catch (e: IOException) {
            e.pushError()
            e.printStackTrace()
            null
        }
    }
}