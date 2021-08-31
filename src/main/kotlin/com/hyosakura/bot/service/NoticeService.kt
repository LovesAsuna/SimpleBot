package com.hyosakura.bot.service

import net.mamoe.mirai.message.data.MessageChain

interface NoticeService : DBService {

    fun getMatchMessage(groupID: Long, targetID: Long): MessageChain?

    fun addNotice(groupID: Long, targetID: Long, message: MessageChain)

    fun removeNotice(groupID: Long, targetID: Long): Boolean
}