package com.hyosakura.bot.service

import com.hyosakura.bot.entity.`fun`.KeyWord

interface KeyWordService : DBService {
    fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean

    fun removeKeyWord(id: Int): Boolean

    fun getKeyWordsByGroup(groupID: Long): List<KeyWord>
}