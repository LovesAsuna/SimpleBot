package com.hyosakura.bot.service

interface DynamicService : DBService {

    fun update(upID: Long, dynamicID: String)

    fun getDynamicID(upID: Long): String
}