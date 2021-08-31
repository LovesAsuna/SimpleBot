package com.hyosakura.bot.controller.photo.source

interface PhotoSource {
    /**
     * 从api获得数据
     * @return 从api获得数据
     */
    fun fetchData(): String?
}