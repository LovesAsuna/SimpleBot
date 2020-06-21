package me.lovesasuna.bot.function.colorphoto

import me.lovesasuna.bot.data.BotData.objectMapper
import me.lovesasuna.bot.util.NetWorkUtil.get
import java.io.IOException

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:06
 */
class Random : Source {
    override fun fetchData(): String? {
        val source = "http://api.mtyqx.cn/api/random.php?return=json"
        val result = get(source)
        return try {
            val root = objectMapper!!.readTree(result!!.first)
            root["imgurl"].asText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}