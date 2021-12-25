package com.hyosakura.bot.controller.picture.hpicture

/**
 * @author LovesAsuna
 **/
interface MultiPictureSource : SinglePictureSource {
    suspend fun fetchData(num: Int): List<String>

    override suspend fun fetchData(): String? = fetchData(1).firstOrNull()
}