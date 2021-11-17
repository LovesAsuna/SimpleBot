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
object Groups : Table<Group>("GROUP") {
    val id = long("ID").primaryKey().bindTo { it.id }
    val name = varchar("NAME").bindTo { it.name }
}

interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()

    var id: Long
    var name: String
}

val Database.groups get() = this.sequenceOf(Groups)