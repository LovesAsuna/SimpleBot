package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.GroupRecordDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.message.Message
import com.hyosakura.bot.service.GroupRecordService
import org.ktorm.database.Database
import java.time.LocalTime

object GroupRecordImpl : GroupRecordService {
    override val database: Database = BotData.messageDatabase
    private val dao: GroupRecordDao by lazy { GroupRecordDao(database) }

    override fun groupIsNull(groupID: Long): Boolean = dao.queryGroup(groupID) == null

    override fun memberIsNull(memberID: Long): Boolean = dao.queryMember(memberID) == null

    override fun relationIsNull(memberID: Long, groupID: Long): Boolean =
        dao.queryRelation(memberID, groupID) == null

    override fun addGroup(groupID: Long, name: String): Boolean {
        return database.useTransaction {
            if (groupIsNull(groupID)) {
                dao.addGroup(groupID, name)
            } else {
                -1
            }
        } > 0
    }

    override fun addMember(memberID: Long, name: String): Boolean {
        return database.useTransaction {
            if (memberIsNull(memberID)) {
                dao.addMember(memberID, name)
            } else {
                -1
            }
        } > 0
    }

    override fun addRecord(message: String, time: LocalTime, memberID: Long, groupID: Long): Boolean {
        return database.useTransaction {
            dao.addRecord(message, time, memberID, groupID)
        } > 0
    }

    override fun addRelation(memberID: Long, groupID: Long): Boolean {
        return database.useTransaction {
            if (relationIsNull(memberID, groupID)) {
                dao.addRelation(memberID, groupID)
            } else {
                -1
            }
        } > 0
    }

    override fun queryUserRecord(memberID: Long): List<Message> = dao.queryUserRecord(memberID)

    override fun queryGroupRecord(groupID: Long): List<Message> = dao.queryGroupRecord(groupID)

    override fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<Message> =
        dao.queryUserRecordInGroup(memberID, groupID)

}

