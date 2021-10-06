package com.hyosakura.bot.controller.picture.hpicture

/**
 * @author LovesAsuna
 **/
interface MultiPictureSource : SinglePictureSource {
    fun fetchData(num : Int): List<String>

    override fun fetchData() : String? = fetchData(1).firstOrNull()
}