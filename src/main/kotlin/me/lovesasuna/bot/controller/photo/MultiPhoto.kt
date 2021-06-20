package me.lovesasuna.bot.controller.photo

import me.lovesasuna.bot.controller.photo.source.PhotoSource

/**
 * @author LovesAsuna
 **/
interface MultiPhoto : PhotoSource {
    fun fetchData(num : Int): List<String>?

    override fun fetchData(): String? {
        return fetchData(1)?.getOrNull(0)
    }
}