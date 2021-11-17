package com.hyosakura.bot.entity.`fun`

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

/**
 * @author LovesAsuna
 **/
object KeyWords : Table<KeyWord>("KEYWORD") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val groupId = long("GROUP_ID").bindTo { it.groupId }
    val wordRegex = varchar("WORD_REGEX").bindTo { it.wordRegex }
    val reply = varchar("REPLY").bindTo { it.reply }
    val chance = int("CHANCE").bindTo { it.chance }
}

interface KeyWord : Entity<KeyWord> {
    companion object : Entity.Factory<KeyWord>()

    val id: Int
    var groupId: Long
    var wordRegex: String
    var reply: String
    var chance: Int
}

val Database.keyWords get() = this.sequenceOf(KeyWords)