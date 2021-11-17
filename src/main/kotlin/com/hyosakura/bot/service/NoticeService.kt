package com.hyosakura.bot.service

import net.mamoe.mirai.message.data.MessageChain

interface NoticeService : DBService {

    fun getMatchMessage(groupId: Long, targetId: Long): MessageChain?

    fun addNotice(groupId: Long, targetId: Long, message: MessageChain): Boolean

    fun removeNotice(groupId: Long, targetId: Long): Boolean
}