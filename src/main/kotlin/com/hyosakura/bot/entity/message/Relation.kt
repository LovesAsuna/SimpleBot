package com.hyosakura.bot.entity.message

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long

/**
 * @author LovesAsuna
 */
object Relations : Table<Relation>("RELATION") {
    val id = long("ID").primaryKey().bindTo { it.id }
    val groupId = long("GROUP_ID").bindTo { it.groupId }
    val memberId = long("MEMBER_ID").bindTo { it.memberId }
}

interface Relation : Entity<Relation> {
    companion object : Entity.Factory<Relation>()

    val id: Long
    var groupId: Long
    var memberId: Long
}

val Database.relations get() = this.sequenceOf(Relations)