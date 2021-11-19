package com.hyosakura.bot.entity.`fun`

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author LovesAsuna
 **/
object Notices : IntIdTable("notice") {
    val groupId = long("group_id")
    val targetId = long("target_id")
    val message = varchar("message", 255)
}

class Notice(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Notice>(Notices)

    var groupId by Notices.groupId
    var targetId by Notices.targetId
    var message by Notices.message
}