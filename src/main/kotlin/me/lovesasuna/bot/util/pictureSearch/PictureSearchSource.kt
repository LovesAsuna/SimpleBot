package me.lovesasuna.bot.util.pictureSearch


interface PictureSearchSource {
    fun search(url: String): List<Result>
}