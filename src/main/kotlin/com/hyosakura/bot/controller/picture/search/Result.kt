package com.hyosakura.bot.controller.picture.search

data class PictureResult(
    var similarity: Double? = null,
    var thumbnail: String? = null,
    var extUrls: List<String>? = null,
    var memberName: String? = null
)

data class AnimeResult(
    var similarity: Double? = null,
    var title: List<String>? = null,
    var from: String? = null,
    var to: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var season: String? = null,
    var episodes: String? = null,
    var duration: String? = null,
    var cover: String? = null,
    var banner: String? = null,
    var extUrls: List<List<String>>? = null,
)