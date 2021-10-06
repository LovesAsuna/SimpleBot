package com.hyosakura.bot.controller.picture.search


interface PictureSearchSource {
    fun search(url: String): List<Result>
}