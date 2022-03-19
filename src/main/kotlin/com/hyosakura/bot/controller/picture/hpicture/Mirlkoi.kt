package com.hyosakura.bot.controller.picture.hpicture

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author LovesAsuna
 */
@Deprecated("图源已失效")
class Mirlkoi : MultiPictureSource {
    override suspend fun fetchData(num: Int): Flow<String> {
        val source = "http://mobile.fgimaxl2.vipnps.vip/API/GHS/3233161559.php"
        return flow {
            repeat(num) {
                emit(source)
            }
        }
    }
}