package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.NoticeDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.`fun`.NoticeEntity
import me.lovesasuna.bot.service.NoticeService
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import org.hibernate.Session

object NoticeServiceImpl : NoticeService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: NoticeDao by lazy { NoticeDao(session) }
    
    override fun getMatchMessage(groupID: Long, targetID: Long): MessageChain? {
        return dao.getMatchMessage(NoticeEntity(groupID = groupID, targetID = targetID))?.deserializeMiraiCode()
    }

    override fun addNotice(groupID: Long, targetID: Long, message: MessageChain) {
        session.transaction.begin()
        dao.addNotice(NoticeEntity(null, groupID, targetID, message.toString()))
        session.transaction.commit()
    }

    override fun removeNotice(groupID: Long, targetID: Long): Boolean {
        session.transaction.begin()
        val dao = dao
        return if (dao.getMatchMessage(NoticeEntity(groupID = groupID, targetID = targetID)) == null) {
            session.transaction.commit()
            false
        } else {
            dao.removeNotice(NoticeEntity(groupID = groupID, targetID = targetID))
            session.transaction.commit()
            true
        }
    }


}