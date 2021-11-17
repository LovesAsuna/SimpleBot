package com.hyosakura.bot.service

interface DynamicService : DBService {

    fun update(upID: Long, dynamicID: String): Int

    fun getDynamicID(upID: Long): String?
}