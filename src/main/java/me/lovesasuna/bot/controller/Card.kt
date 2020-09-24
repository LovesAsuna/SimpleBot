package me.lovesasuna.bot.controller

import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*

class Card : FunctionListener {
    private val map = HashMap<Long, Type>()

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val senderID = event.sender.id
        val at = At(event.sender as Member)
        if (!map.contains(senderID)) {
            when (message) {
                "/makecard" -> {
                    map[senderID] = Type.MakeCard
                    event.reply(at + "请发送Json")
                }
                "/parsecard" -> {
                    map[senderID] = Type.ParseCard
                    event.reply(at + "请发送卡片")
                }
            }
            return true
        }

        if (map[senderID] != null) {
            when (map[senderID]) {
                Type.MakeCard -> {
                    map.remove(senderID)
                    event.reply(LightApp(message))
                }
                Type.ParseCard -> {
                    map.remove(senderID)
                    val app = event.message[RichMessage]
                    if (app == null) {
                        event.reply("无法解析!")
                    } else {
                        event.reply(app.content)
                    }
                }
            }
        }
        return true
    }

    private enum class Type {
        MakeCard,
        ParseCard;
    }
}