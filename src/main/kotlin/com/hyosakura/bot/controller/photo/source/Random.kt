package com.hyosakura.bot.controller.photo.source

import com.hyosakura.bot.data.BotData.objectMapper
import com.hyosakura.bot.util.network.OkHttpUtil
import java.io.IOException

/**
 * @author LovesAsuna
 */
class Random : PhotoSource {
    override fun fetchData(): String? {
        val source = "http://api.mtyqx.cn/api/random.php?return=json"
        val result = OkHttpUtil.getIs(OkHttpUtil[source])
        return try {
            val root = objectMapper.readTree(result)
            root["imgurl"].asText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}