package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.GroupRecordDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.message.MessageEntity
import me.lovesasuna.bot.service.GroupRecordService
import org.hibernate.Session
import java.util.*

object GroupRecordImpl : GroupRecordService {

    override var session: Session = BotData.recordConfig.buildSessionFactory().openSession()

    private val dao: GroupRecordDao by lazy { GroupRecordDao(session) }

    override fun groupIsNull(groupID: Long): Boolean = dao.queryGroup(groupID) == null

    override fun memberIsNull(memberID: Long): Boolean = dao.queryMember(memberID) == null

    override fun participationIsNull(memberID: Long, groupID: Long): Boolean =
        dao.queryParticipation(memberID, groupID) == null

    override fun addGroup(groupID: Long, name: String) {
        session.beginTransaction()
        try {
            dao.addGroup(groupID, name)
        } finally {
            session.transaction.commit()
        }
    }

    override fun addMember(memberID: Long, name: String) {
        session.beginTransaction()
        try {
            dao.addMember(memberID, name)
        } finally {
            session.transaction.commit()
        }
    }

    override fun addRecord(message: String, time: Date, memberID: Long, groupID: Long) {
        session.beginTransaction()
        try {
            dao.addRecord(message, time, memberID, groupID)
        } finally {
            session.transaction.commit()
        }
    }

    override fun addParticipation(memberID: Long, groupID: Long, nickName: String) {
        session.beginTransaction()
        try {
            dao.addParticipation(memberID, groupID, nickName)
        } finally {
            session.transaction.commit()
        }
    }

    override fun updateParticipationNickName(memberID: Long, groupID: Long, nickName: String) {
        val entity = dao.queryParticipation(groupID, memberID)
        if (entity != null && entity.nickname != nickName) {
            entity.nickname = nickName
        }
        session.beginTransaction()
        try {
            session.update(entity)
        } finally {
            session.transaction.commit()
        }
    }

    override fun queryUserRecord(memberID: Long): List<MessageEntity> = dao.queryUserRecord(memberID)

    override fun queryGroupRecord(groupID: Long): List<MessageEntity> = dao.queryGroupRecord(groupID)

    override fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<MessageEntity> =
        dao.queryUserRecordInGroup(memberID, groupID)

}

