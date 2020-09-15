package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.NoticeDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.NoticeEntity
import me.lovesasuna.bot.service.NoticeService
import net.mamoe.mirai.message.data.MessageChain
import org.hibernate.Session

object NoticeServiceImpl : NoticeService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun getMatchMessage(groupID: Long, targetID: Long) = NoticeDao(session).getMatchMessage(groupID, targetID)

    override fun addNotice(groupID: Long, targetID: Long, message: MessageChain) {
        session.beginTransaction()
        val messageChain = BotData.objectMapper.writeValueAsString(message)
        NoticeDao(session).addNotice(NoticeEntity(null, groupID, targetID, messageChain))
        session.transaction.commit()
    }

}