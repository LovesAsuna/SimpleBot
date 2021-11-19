package com.hyosakura.bot.entity.message

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * @author LovesAsuna
 */
object Groups : LongIdTable("group") {
    val name = varchar("name", 50)
}

class Group(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Group>(Groups)

    var name by Groups.name
    var members by Member via Relations
}