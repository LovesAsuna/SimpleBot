package com.hyosakura.bot.service

import me.lovesasuna.bot.entity.`fun`.KeyWordEntity

interface KeyWordService : DBService {
    fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean

    fun removeKeyWord(id: Int): Boolean

    fun getKeyWordsByGroup(groupID: Long): List<KeyWordEntity>
}