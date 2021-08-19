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

    override fun groupIsNull(groupID: Long): Boolean = dao.groupIsNull(groupID)

    override fun memberIsNull(memberID: Long): Boolean = dao.memberIsNull(memberID)

    override fun participationIsNull(memberID: Long, groupID: Long): Boolean =
        dao.participationIsNull(memberID, groupID)

    override fun addGroup(groupID: Long, name: String) {
        session.beginTransaction()
        dao.addGroup(groupID, name)
        session.transaction.commit()
    }

    override fun addMember(memberID: Long, name: String) {
        session.beginTransaction()
        dao.addMember(memberID, name)
        session.transaction.commit()
    }

    override fun addRecord(message: String, time: Date, memberID: Long, groupID: Long) {
        session.beginTransaction()
        dao.addRecord(message, time, memberID, groupID)
        session.transaction.commit()
    }

    override fun addParticipation(groupID: Long, memberID: Long, nickName: String) {
        session.beginTransaction()
        dao.addParticipation(groupID, memberID, nickName)
        session.transaction.commit()
    }

    override fun queryUserRecord(memberID: Long): List<MessageEntity> = dao.queryUserRecord(memberID)

    override fun queryGroupRecord(groupID: Long): List<MessageEntity> = dao.queryGroupRecord(groupID)

    override fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<MessageEntity> =
        dao.queryUserRecordInGroup(memberID, groupID)

}

