package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.util.network.Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author LovesAsuna
 */
class Lolicon : MultiPictureSource {
    override suspend fun fetchData(num: Int): Flow<String> {
        val source = "https://api.lolicon.app/setu/v2?num=${if (num > 10) 10 else num}"
        val root = Request.getJson(source)
        return flow {
            repeat(num) {
                val url = root["data"][it]["urls"]["original"].asText().replace("cat", "re")
                emit(url)
            }
        }
    }
}