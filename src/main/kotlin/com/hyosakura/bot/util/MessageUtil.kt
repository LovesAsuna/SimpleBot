package com.hyosakura.bot.util

import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.RawForwardMessage

object MessageUtil {
    fun buildForwardsMessage(
        user : User,
        messages : List<Message>,
        strategy : ForwardMessage.DisplayStrategy = ForwardMessage.DisplayStrategy
    ) : ForwardMessage {
        val time = (System.currentTimeMillis() / 1000).toInt()
        val nodeList =  messages.map {
            ForwardMessage.Node(user.id, time, user.nameCardOrNick, it)
        }
        return RawForwardMessage(nodeList).render(strategy)
    }

}