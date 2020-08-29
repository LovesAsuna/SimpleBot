package me.lovesasuna.bot.util.interfaces

interface PhotoSource {
    /**
     * 从api获得数据
     * @return 从api获得数据
     */
    fun fetchData(): String?
}