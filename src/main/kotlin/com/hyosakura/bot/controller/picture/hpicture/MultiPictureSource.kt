package com.hyosakura.bot.controller.picture.hpicture

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * @author LovesAsuna
 **/
interface MultiPictureSource : SinglePictureSource {
    suspend fun fetchData(num: Int): Flow<String>

    override suspend fun fetchData(): String? = fetchData(1).firstOrNull()
}