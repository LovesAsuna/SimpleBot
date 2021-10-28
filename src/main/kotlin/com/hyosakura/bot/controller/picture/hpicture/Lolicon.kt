package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.util.network.OkHttpUtil
import java.io.IOException

/**
 * @author LovesAsuna
 */
class Lolicon : MultiPictureSource {
    override fun fetchData(num: Int): List<String> {
        val source = "https://api.lolicon.app/setu/v2?num=${if (num > 10) 10 else num}"
        val result = OkHttpUtil.getIs(OkHttpUtil[source])
        val root = BotData.objectMapper.readTree(result)
        return mutableListOf<String>().apply {
            try {
                repeat(num) {
                    val url = root["data"][it]["urls"]["original"].asText().replace("cat", "re")
                    this.add(url)
                }
            } catch (e: IOException) {
                Main.logger.error(e)
            } catch (e: NullPointerException) {
                Main.logger.error(e)
            }
        }
    }
}