package me.lovesasuna.bot.dao

import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.NoticeEntity
import net.mamoe.mirai.message.data.MessageChain
import org.hibernate.Session

/**
 * @author LovesAsuna
 * @date 2020/9/13 20:27
 **/
class NoticeDao(override val session: Session) : DefaultHibernateDao<NoticeEntity>(session) {
    fun getMatchMessage(groupID: Long, targetID: Long): MessageChain? {
        return queryField("select distinct e.message from NoticeEntity as e where groupID = $groupID and targetID = 1", String::class.java).let {
            if (it.isEmpty()) {
                null
            } else {
                BotData.objectMapper.readValue(it[0], MessageChain::class.java)
            }
        }
    }

    fun addNotice(entity: NoticeEntity) {
        session.saveOrUpdate(entity)
    }
}