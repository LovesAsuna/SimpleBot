package com.hyosakura.bot.entity.message

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

/**
 * @author LovesAsuna
 */
object Members : Table<Member>("MEMBER") {
    val id = long("ID").primaryKey().bindTo { it.id }
    val name = varchar("NAME").bindTo { it.name }
}

interface Member : Entity<Member> {
    companion object : Entity.Factory<Member>()

    var id: Long
    var name: String
}

val Database.members get() = this.sequenceOf(Members)