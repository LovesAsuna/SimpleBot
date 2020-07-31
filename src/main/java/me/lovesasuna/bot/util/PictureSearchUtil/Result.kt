package me.lovesasuna.bot.util.PictureSearchUtil

data class Result(val similarity: Int, val thumbnail: String, val extUrls: List<String>, val memberName: String)