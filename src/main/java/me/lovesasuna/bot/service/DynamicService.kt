package me.lovesasuna.bot.service

interface DynamicService : DBService {

    fun update(upID: Long, context: String)

    fun getContext(upID: Long): String
}