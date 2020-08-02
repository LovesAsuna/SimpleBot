package me.lovesasuna.bot.util.interfaces

import me.lovesasuna.bot.util.pictureSearchUtil.Result


interface PictureSearchSource {
     fun search(url: String): List<Result>
}