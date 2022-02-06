package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.util.network.Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author LovesAsuna
 */
class Mirlkoi : MultiPictureSource {
    override suspend fun fetchData(num: Int): Flow<String> {
        val source = "http://mobile.fgimaxl2.vipnps.vip/API/GHS/3233161559.php?type=json"
        return flow {
            repeat(num) {
                val root = Request.getJson(source)
                val url = root["pic"].asText()
                emit(url)
            }
        }
    }
}