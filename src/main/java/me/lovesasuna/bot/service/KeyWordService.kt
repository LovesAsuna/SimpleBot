package me.lovesasuna.bot.service

import me.lovesasuna.bot.entity.database.KeyWordEntity

interface KeyWordService : DBService {
    fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean

    fun removeKeyWord(id: Int): Boolean

    fun getKeyWordsByGroup(groupID: Long): List<KeyWordEntity>
}