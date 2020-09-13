package me.lovesasuna.bot.service

interface DynamicService : Service {

    fun update(upID: Int, context: String)

    fun getContext(upID: Int): String
}