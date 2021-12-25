package com.hyosakura.bot.controller.picture.hpicture

import kotlin.random.Random

/**
 * @author LovesAsuna
 **/
class Girl : SinglePictureSource {
    override suspend fun fetchData(): String {
        return "https://api.nmb.show/xiaojiejie${Random.nextInt(1, 3)}.php"
    }
}