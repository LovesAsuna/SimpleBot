package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.NoticeEntity
import org.hibernate.Session

/**
 * @author LovesAsuna
 * @date 2020/9/13 20:27
 **/
class NoticeDao(override val session: Session) : DefaultHibernateDao<NoticeEntity>(session) {
    fun getMatchMessage(groupID: Long, targetID: Long): String? {
        return queryField("select distinct e.message from NoticeEntity as e where groupID = ?1 and targetID = ?2", String::class.java, groupID, targetID).let {
            if (it.isEmpty()) {
                null
            } else {
                it[0]
            }
        }
    }

    fun addNotice(entity: NoticeEntity) {
        session.saveOrUpdate(entity)
    }
}