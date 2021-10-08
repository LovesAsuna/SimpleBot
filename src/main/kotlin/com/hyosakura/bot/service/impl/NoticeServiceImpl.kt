package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.NoticeDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.`fun`.NoticeEntity
import com.hyosakura.bot.service.AutoRegisterDBService
import com.hyosakura.bot.service.NoticeService
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import org.hibernate.Session

object NoticeServiceImpl : AutoRegisterDBService(), NoticeService {
    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()
    private val dao: NoticeDao by lazy { NoticeDao(session) }

    override fun getMatchMessage(groupID: Long, targetID: Long): MessageChain? {
        return dao.getMatchMessage(NoticeEntity(groupID = groupID, targetID = targetID))?.deserializeMiraiCode()
    }

    override fun addNotice(groupID: Long, targetID: Long, message: MessageChain) {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            dao.addNotice(NoticeEntity(null, groupID, targetID, message.toString()))
        } finally {
            session.transaction.commit()
        }
    }

    override fun removeNotice(groupID: Long, targetID: Long): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            return if (dao.getMatchMessage(NoticeEntity(groupID = groupID, targetID = targetID)) == null) {
                false
            } else {
                dao.removeNotice(NoticeEntity(groupID = groupID, targetID = targetID))
                true
            }
        } finally {
            session.transaction.commit()
        }
    }
}