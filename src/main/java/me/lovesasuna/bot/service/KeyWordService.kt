package me.lovesasuna.bot.service

import me.lovesasuna.bot.entity.KeyWordEntity

interface KeyWordService : Service {
    fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean

    fun removeKeyWord(groupID: Long, wordRegex: String): Boolean

    fun getKeyWordsByGroup(groupID: Long): List<KeyWordEntity>
}