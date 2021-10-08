package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.GroupRecordDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.message.MessageEntity
import com.hyosakura.bot.service.AutoRegisterDBService
import com.hyosakura.bot.service.GroupRecordService
import org.hibernate.Session
import java.util.*

object GroupRecordImpl : AutoRegisterDBService(), GroupRecordService {
    override var session: Session = BotData.recordConfig.buildSessionFactory().openSession()
    private val dao: GroupRecordDao by lazy { GroupRecordDao(session) }

    override fun groupIsNull(groupID: Long): Boolean = dao.queryGroup(groupID) == null

    override fun memberIsNull(memberID: Long): Boolean = dao.queryMember(memberID) == null

    override fun participationIsNull(memberID: Long, groupID: Long): Boolean =
        dao.queryParticipation(memberID, groupID) == null

    override fun addGroup(groupID: Long, name: String): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            dao.addGroup(groupID, name)
            return true
        } finally {
            session.transaction.commit()
        }
    }

    override fun addMember(memberID: Long, name: String): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            dao.addMember(memberID, name)
            return true
        } finally {
            session.transaction.commit()
        }
    }

    override fun addRecord(message: String, time: Date, memberID: Long, groupID: Long): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            dao.addRecord(message, time, memberID, groupID)
            return true
        } finally {
            session.transaction.commit()
        }
    }

    override fun addParticipation(memberID: Long, groupID: Long): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            dao.addParticipation(memberID, groupID)
            return true
        } finally {
            session.transaction.commit()
        }
    }

    override fun queryUserRecord(memberID: Long): List<MessageEntity> = dao.queryUserRecord(memberID)

    override fun queryGroupRecord(groupID: Long): List<MessageEntity> = dao.queryGroupRecord(groupID)

    override fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<MessageEntity> =
        dao.queryUserRecordInGroup(memberID, groupID)

}

