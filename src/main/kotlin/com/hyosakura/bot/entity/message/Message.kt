package com.hyosakura.bot.entity.message

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.time

/**
 * @author LovesAsuna
 **/
object Messages : IntIdTable("message") {
    val content = text("content")
    val time = time("time")
    val member = reference("member_id", Members)
    val group = reference("group_id", Groups)
}

class Message(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Message>(Messages)

    var content by Messages.content
    var time by Messages.time
    var member by Member referencedOn Messages.member
    var group by Group referencedOn Messages.group
}