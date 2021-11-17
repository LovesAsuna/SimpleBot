package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.message.*
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
import java.time.LocalTime

/**
 * @author LovesAsuna
 **/
class GroupRecordDao(override val database: Database) : DefaultDao {
    fun queryGroup(groupID: Long): Group? {
        return database.groups.find {
            it.id eq groupID
        }
    }

    fun queryMember(memberID: Long): Member? {
        return database.members.find {
            it.id eq memberID
        }
    }

    fun queryRelation(memberID: Long, groupID: Long): Relation? {
        return database.relations.find {
            (it.memberId eq memberID) and (it.groupId eq groupID)
        }
    }

    fun addGroup(groupID: Long, groupName: String): Int {
        val group = Group {
            id = groupID
            name = groupName
        }
        return database.groups.add(group)
    }

    fun addMember(memberID: Long, memberName: String): Int {
        val member = Member {
            id = memberID
            name = memberName
        }
        return database.members.add(member)
    }

    fun addRecord(content: String, time: LocalTime, memberID: Long, groupID: Long): Int {
        val message = Message {
            this.content = content
            this.time = time
            member = database.members.find { it.id eq memberID }!!
            group = database.groups.find { it.id eq groupID }!!
        }
        return database.messages.add(message)
    }

    fun addRelation(memberID: Long, groupID: Long): Int {
        val relation = Relation {
            this.groupId = groupID
            this.memberId = memberID
        }
        return database.relations.add(relation)
    }

    fun queryUserRecord(memberID: Long): List<Message> {
        return database.messages.filter {
            it.memberId eq memberID
        }.toList()
    }

    fun queryGroupRecord(groupID: Long): List<Message> {
        return database.messages.filter {
            it.groupId eq groupID
        }.toList()
    }

    fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<Message> {
        return database.messages.filter {
            (it.memberId eq memberID) and (it.groupId eq groupID)
        }.toList()
    }
}