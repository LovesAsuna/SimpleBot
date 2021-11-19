package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.message.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.LocalTime

/**
 * @author LovesAsuna
 **/
class GroupRecordDao(override val database: Database) : DefaultDao {
    fun queryGroup(groupID: Long): Group? {
        return Group.findById(groupID)
    }

    fun queryMember(memberID: Long): Member? {
        return Member.findById(memberID)
    }

    fun queryRelation(memberID: Long, groupID: Long): Boolean {
        return Relations.select {
            Relations.member eq memberID and (Relations.group eq groupID)
        }.any()
    }

    fun addGroup(groupID: Long, groupName: String): Long {
        return Group.new(groupID) {
            name = groupName
        }.id.value
    }

    fun addMember(memberID: Long, memberName: String): Long {
        return Member.new(memberID) {
            name = memberName
        }.id.value
    }

    fun addRecord(content: String, time: LocalTime, memberID: Long, groupID: Long): Int {
        return Message.new {
            this.content = content
            this.time = time
            this.member = Member.findById(memberID)!!
            this.group = Group.findById(groupID)!!
        }.id.value
    }

    fun addRelation(memberID: Long, groupID: Long): Int {
        return Relations.insert {
            it[this.member] = Member.findById(memberID)!!.id
            it[this.group] = Group.findById(groupID)!!.id
        }.insertedCount
    }

    fun queryUserRecord(memberID: Long): List<Message> {
        return Message.find {
            Messages.member eq memberID
        }.toList()
    }

    fun queryGroupRecord(groupID: Long): List<Message> {
        return Message.find {
            Messages.group eq groupID
        }.toList()
    }

    fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<Message> {
        return Message.find {
            (Messages.group eq groupID) and (Messages.member eq memberID)
        }.toList()
    }
}