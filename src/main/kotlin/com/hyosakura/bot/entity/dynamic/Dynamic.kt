package com.hyosakura.bot.entity.dynamic

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
object Dynamics : Table<Dynamic>("DYNAMIC") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val upId = long("UP_ID").bindTo { it.upId }
    val dynamicId = varchar("DYNAMIC_ID").bindTo { it.dynamicId }
}

interface Dynamic : Entity<Dynamic> {
    companion object : Entity.Factory<Dynamic>()

    val id: Int
    val upId: Long
    val dynamicId: String
}

val Database.dynamics get() = this.sequenceOf(Dynamics)