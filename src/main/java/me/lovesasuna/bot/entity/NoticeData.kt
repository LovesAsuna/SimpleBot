package me.lovesasuna.bot.entity

import net.mamoe.mirai.message.data.MessageChain
import java.io.Serializable
import java.util.ArrayList

data class NoticeData(var msgList: ArrayList<Triple<Long, Long, MessageChain>>) : Serializable
