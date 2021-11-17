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
object Notices : Table<Notice>("NOTICE") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val groupId = long("GROUP_ID").bindTo { it.groupId }
    val targetId = long("TARGET_ID").bindTo { it.targetId }
    val message = varchar("MESSAGE").bindTo { it.message }
}

interface Notice : Entity<Notice> {
    companion object : Entity.Factory<Notice>()

    val id: Int
    var groupId: Long
    var targetId: Long
    var message: String
}

val Database.notices get() = this.sequenceOf(Notices)