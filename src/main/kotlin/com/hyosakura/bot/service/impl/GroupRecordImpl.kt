package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.GroupRecordDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.message.Message
import com.hyosakura.bot.service.GroupRecordService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object GroupRecordImpl : GroupRecordService {
    override val database: Database = BotData.messageDatabase
    private val dao: GroupRecordDao by lazy { GroupRecordDao(database) }

    override fun groupIsNull(groupID: Long): Boolean = transaction(database) {
        dao.queryGroup(groupID) == null
    }

    override fun memberIsNull(memberID: Long): Boolean = transaction(database) {
        dao.queryMember(memberID) == null
    }

    override fun relationIsNull(memberID: Long, groupID: Long): Boolean = transaction(database) {
        dao.queryRelation(memberID, groupID)
    }

    override fun addGroup(groupID: Long, name: String): Boolean = transaction(database) {
        if (groupIsNull(groupID)) {
            dao.addGroup(groupID, name)
        } else {
            -1
        } > 0
    }

    override fun addMember(memberID: Long, name: String): Boolean = transaction(database) {
        if (memberIsNull(memberID)) {
            dao.addMember(memberID, name)
        } else {
            -1
        } > 0
    }

    override fun addRecord(message: String, time: LocalDateTime, memberID: Long, groupID: Long): Boolean = transaction(database) {
        dao.addRecord(message, time, memberID, groupID) > 0
    }

    override fun addRelation(memberID: Long, groupID: Long): Boolean = transaction(database) {
        if (relationIsNull(memberID, groupID)) {
            dao.addRelation(memberID, groupID)
        } else {
            -1
        } > 0
    }

    override fun queryUserRecord(memberID: Long): List<Message> = transaction(database) {
        dao.queryUserRecord(memberID)
    }

    override fun queryGroupRecord(groupID: Long): List<Message> = transaction(database) {
        dao.queryGroupRecord(groupID)
    }

    override fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<Message> = transaction(database) {
        dao.queryUserRecordInGroup(memberID, groupID)
    }
}

