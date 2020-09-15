package me.lovesasuna.bot.service

interface DynamicService : Service {

    fun update(upID: Long, context: String)

    fun getContext(upID: Long): String
}