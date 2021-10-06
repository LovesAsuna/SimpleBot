package com.hyosakura.bot.controller.picture.search

data class Result(
    val similarity: Int,
    val thumbnail: String,
    val extUrls: List<String>,
    val memberName: String
)