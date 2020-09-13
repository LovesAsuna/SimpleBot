package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.NoticeDao
import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.entity.NoticeEntity
import me.lovesasuna.bot.service.NoticeService
import net.mamoe.mirai.message.data.MessageChain
import org.hibernate.SessionFactory

object NoticeServiceImpl : NoticeService {

    override val factory: SessionFactory = BotData.HibernateConfig.buildSessionFactory()

    override fun getMatchMessage(groupID: Long, targetID: Long): MessageChain? {
        val session = LinkServiceImpl.factory.openSession()
        session.use { s ->
            return NoticeDao(s).getMatchMessage(groupID, targetID)
        }
    }

    override fun addNotice(groupID: Long, targetID: Long, message: MessageChain) {
        val session = LinkServiceImpl.factory.openSession()
        session.beginTransaction()
        val messageChain = BotData.objectMapper.writeValueAsString(message)
        NoticeDao(session).addNotice(NoticeEntity(null, groupID, targetID, messageChain))
        session.transaction.commit()
        session.close()
    }

}