package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.message.*
import me.lovesasuna.bot.service.GroupRecordService
import org.hibernate.Session
import java.util.*

/**
 * @author LovesAsuna
 **/
class GroupRecordDao(override val session: Session) : DefaultHibernateDao<MessageEntity>(session), GroupRecordService {
    override fun groupIsNull(groupID: Long): Boolean {
        return session.get(GroupEntity::class.java, groupID) == null
    }

    override fun memberIsNull(memberID: Long): Boolean {
        return session.get(MemberEntity::class.java, memberID) == null
    }

    override fun participationIsNull(memberID: Long, groupID: Long): Boolean {
        return session.get(ParticipationEntity::class.java, GroupAndMember(groupID, memberID)) == null
    }

    override fun addGroup(groupID: Long, name: String) {
        session.saveOrUpdate(GroupEntity(groupID, name))
    }

    override fun addMember(memberID: Long, name: String) {
        session.saveOrUpdate(MemberEntity(memberID, name))
    }

    override fun addRecord(message: String, time: Date, memberID: Long, groupID: Long) {
        session.saveOrUpdate(MessageEntity(null, message, time, MemberEntity(memberID), GroupEntity(groupID)))
    }

    override fun addParticipation(groupID: Long, memberID: Long, nickName: String) {
        session.saveOrUpdate(ParticipationEntity(groupID, memberID, nickName))
    }

    override fun queryUserRecord(memberID: Long): List<MessageEntity> {
        return queryEntity(
            "from MessageEntity message where message.member.id = ?1",
            MessageEntity::class.java,
            memberID
        )
    }

    override fun queryGroupRecord(groupID: Long): List<MessageEntity> {
        return queryEntity("from MessageEntity message where message.group.id = ?1", MessageEntity::class.java, groupID)
    }

    override fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<MessageEntity> {
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