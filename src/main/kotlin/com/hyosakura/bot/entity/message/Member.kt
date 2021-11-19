package com.hyosakura.bot.entity.message

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * @author LovesAsuna
 */
object Members : LongIdTable("member") {
    val name = varchar("name", 50)
}

class Member(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Member>(Members)

    var name by Members.name
    var groups by Group via Relations
}