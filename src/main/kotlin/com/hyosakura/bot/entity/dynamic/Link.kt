package com.hyosakura.bot.entity.dynamic

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long

/**
 * @author LovesAsuna
 **/
object Links : Table<Link>("link") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val groupId = long("GROUP_ID").bindTo { it.groupId }
    // TODO 无法绑定Dynamic表的非主键
    val upId = long("UP_ID").references(Dynamics) { it.dynamic }
}

interface Link : Entity<Link> {
    companion object : Entity.Factory<Link>()

    val id: Int
    var groupId: Long
    var dynamic: Dynamic
}

val Database.links get() = this.sequenceOf(Links)