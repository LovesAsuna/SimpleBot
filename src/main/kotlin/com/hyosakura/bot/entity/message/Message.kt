package com.hyosakura.bot.entity.message

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalTime

/**
 * @author LovesAsuna
 **/
object Messages : Table<Message>("MESSAGE") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val content = varchar("CONTENT").bindTo { it.content }
    val time = time("TIME").bindTo { it.time }
    val memberId = long("MEMBER_ID").references(Members) { it.member }
    val groupId = long("GROUP_ID").references(Groups) { it.group }
}

interface Message : Entity<Message> {
    companion object : Entity.Factory<Message>()

    var id: Int
    var content: String
    var time: LocalTime
    var member: Member
    var group: Group
}

val Database.messages get() = this.sequenceOf(Messages)