package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.NoticeDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.NoticeEntity
import me.lovesasuna.bot.service.NoticeService
import net.mamoe.mirai.message.code.parseMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.messageChainOf
import org.hibernate.Hibernate
import org.hibernate.Session
import org.omg.CORBA.Object
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object NoticeServiceImpl : NoticeService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun getMatchMessage(groupID: Long, targetID: Long): MessageChain? {
        return NoticeDao(session).getMatchMessage(groupID, targetID)?.parseMiraiCode()
    }

    override fun addNotice(groupID: Long, targetID: Long, message: MessageChain) {
        session.beginTransaction()
        NoticeDao(session).addNotice(NoticeEntity(null, groupID, targetID, message.toString()))
        session.transaction.commit()
    }

}