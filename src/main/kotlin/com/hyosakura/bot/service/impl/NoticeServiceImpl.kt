package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.NoticeDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.NoticeService
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import org.ktorm.database.Database

object NoticeServiceImpl : NoticeService {
    override val database: Database = BotData.botDatabase
    private val dao: NoticeDao by lazy { NoticeDao(database) }

    override fun getMatchMessage(groupId: Long, targetId: Long): MessageChain? {
        return database.useTransaction {
            dao.getMatchMessage(groupId, targetId)?.deserializeMiraiCode()
        }
    }

    override fun addNotice(groupId: Long, targetId: Long, message: MessageChain): Boolean {
        return database.useTransaction {
            dao.addNotice(groupId, targetId, message.serializeToMiraiCode())
        } > 0
    }

    override fun removeNotice(groupId: Long, targetId: Long): Boolean {
        return database.useTransaction {
            dao.removeNotice(groupId, targetId)
        } > 0
    }
}