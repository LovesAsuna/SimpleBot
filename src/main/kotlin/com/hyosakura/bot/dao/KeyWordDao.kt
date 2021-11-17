package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.`fun`.KeyWord
import com.hyosakura.bot.entity.`fun`.keyWords
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

/**
 * @author LovesAsuna
 **/
class KeyWordDao(override val database: Database) : DefaultDao {
    fun checkKeyWordExist(groupID: Long, wordRegex: String): Boolean {
        return database.keyWords.find {
            (it.groupId eq groupID) and (it.wordRegex eq wordRegex)
        } != null
    }

    fun checkKeyWordExist(id: Int): Boolean {
        return database.keyWords.find {
            it.id eq id
        } != null
    }

    fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Int {
        val keyword = KeyWord {
            this.groupId = groupID
            this.wordRegex = wordRegex
            this.reply = reply
            this.chance = chance
        }
        return database.keyWords.add(keyword)
    }

    fun removeKeyWordById(id : Int): Int {
        return database.keyWords.removeIf {
            it.id eq id
        }
    }

    fun getKeyWordsByGroup(groupID: Long): List<KeyWord> {
        return database.keyWords.filter {
            it.groupId eq groupID
        }.toList()
    }
}