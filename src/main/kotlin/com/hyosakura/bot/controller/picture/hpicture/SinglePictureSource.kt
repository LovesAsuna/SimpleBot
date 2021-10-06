package com.hyosakura.bot.controller.picture.hpicture

interface SinglePictureSource {
    /**
     * 从api获得数据
     * @return 从api获得数据
     */
    fun fetchData(): String?
}