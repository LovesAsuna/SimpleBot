package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.message.*
import org.hibernate.Session
import java.util.*

/**
 * @author LovesAsuna
 **/
class GroupRecordDao(override val session: Session) : DefaultHibernateDao<MessageEntity>(session) {
    fun queryGroup(groupID: Long): GroupEntity? {
        return session.get(GroupEntity::class.java, groupID)
    }

    fun queryMember(memberID: Long): MemberEntity? {
        return session.get(MemberEntity::class.java, memberID)
    }

    fun queryParticipation(memberID: Long, groupID: Long): ParticipationEntity? {
        return session.get(ParticipationEntity::class.java, GroupAndMember(groupID, memberID))
    }

    fun addGroup(groupID: Long, name: String) {
        session.saveOrUpdate(GroupEntity(groupID, name))
    }

    fun addMember(memberID: Long, name: String) {
        session.saveOrUpdate(MemberEntity(memberID, name))
    }

    fun addRecord(message: String, time: Date, memberID: Long, groupID: Long) {
        session.saveOrUpdate(MessageEntity(null, message, time, MemberEntity(memberID), GroupEntity(groupID)))
    }

    fun addParticipation(memberID: Long, groupID: Long) {
        session.saveOrUpdate(ParticipationEntity(groupID, memberID))
    }

    fun queryUserRecord(memberID: Long): List<MessageEntity> {
        return queryEntity(
            "from MessageEntity message where message.member.id = ?1",
            MessageEntity::class.java,
            memberID
        )
    }

    fun queryGroupRecord(groupID: Long): List<MessageEntity> {
        return queryEntity("from MessageEntity message where message.group.id = ?1", MessageEntity::class.java, groupID)
    }

    fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<MessageEntity> {
        return queryEntity(
            "select message from " +
                    "MessageEntity as message " +
                    "join GroupEntity as group " +
                    "join MemberEntity as member " +
                    "where member.id = ?1 and group.id = ?2", MessageEntity::class.java,
            memberID, groupID
        )
    }
}