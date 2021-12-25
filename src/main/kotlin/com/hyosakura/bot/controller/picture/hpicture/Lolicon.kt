package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.Request
import java.io.IOException

/**
 * @author LovesAsuna
 */
class Lolicon : MultiPictureSource {
    override suspend fun fetchData(num: Int): List<String> {
        val source = "https://api.lolicon.app/setu/v2?num=${if (num > 10) 10 else num}"
        val root = Request.getJson(source)
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