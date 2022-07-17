package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.`fun`.KeyWord
import com.hyosakura.bot.entity.`fun`.KeyWords
import org.jetbrains.exposed.sql.*


/**
 * @author LovesAsuna
 **/
class KeyWordDao(override val database: Database) : DefaultDao {
    fun checkKeyWordExist(groupID: Long, wordRegex: String): Boolean {
        return KeyWords.select {
            (KeyWords.groupId eq groupID) and (KeyWords.wordRegex eq wordRegex)
        }.any()
    }

    fun checkKeyWordExist(id: Int): Boolean {
        return KeyWord.findById(id) != null
    }

    fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Int {
        val maxId = KeyWords.slice(KeyWords.id).selectAll().maxOf {
            it[KeyWords.id]
        }.value
        return KeyWord.new(maxId + 1) {
            this.groupId = groupID
            this.wordRegex = wordRegex
            this.reply = reply
            this.chance = chance
        }.id.value
    }

    fun removeKeyWordById(id: Int): Int {
        return KeyWords.deleteWhere { KeyWords.id eq id }
    }

    fun getKeyWordsByGroup(groupID: Long): List<KeyWord> {
        return KeyWords.select { KeyWords.groupId eq groupID }.map {
            KeyWord.wrapRow(it)
        }
    }
}