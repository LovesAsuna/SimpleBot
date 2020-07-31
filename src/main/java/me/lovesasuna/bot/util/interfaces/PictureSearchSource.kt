package me.lovesasuna.bot.util.interfaces

import me.lovesasuna.bot.util.PictureSearchUtil.Result


interface PictureSearchSource {
     fun search(url: String): List<Result>
}