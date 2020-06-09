package me.lovesasuna.bot.function.colorphoto

interface Source {
    /**
     * 从api获得数据
     * @return 从api获得数据
     */
    fun fetchData(): String?
}