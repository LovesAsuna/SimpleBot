package com.hyosakura.bot.entity.`fun`

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author LovesAsuna
 **/
object KeyWords : IntIdTable("keyword") {
    val groupId = long("group_id")
    val wordRegex = varchar("word_regex", 50)
    val reply = varchar("reply", 50)
    val chance = integer("change")
}

class KeyWord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KeyWord>(KeyWords)

    var groupId by KeyWords.groupId
    var wordRegex by KeyWords.wordRegex
    var reply by KeyWords.reply
    var chance by KeyWords.chance
}