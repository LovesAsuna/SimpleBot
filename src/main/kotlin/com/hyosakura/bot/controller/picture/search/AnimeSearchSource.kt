package com.hyosakura.bot.controller.picture.search


interface AnimeSearchSource {
    fun search(url: String): List<AnimeResult>
}