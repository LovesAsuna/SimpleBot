package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.RichMessage

class Card : FunctionListener {
    private val map = HashMap<Long, Type>()

    override suspend fun execute(box: MessageBox): Boolean {
        val senderID = box.sender.id
        val at = At(box.sender as Member)
        if (!map.contains(senderID)) {
            when (box.text()) {
                "/makecard" -> {
                    map[senderID] = Type.MakeCard
                    box.reply(at + "请发送Json")
                }
                "/parsecard" -> {
                    map[senderID] = Type.ParseCard
                    box.reply(at + "请发送卡片")
                }
            }
            return true
        }

        if (map[senderID] != null) {
            when (map[senderID]) {
                Type.MakeCard -> {
                    map.remove(senderID)
                    box.reply(LightApp(box.text()))
                }
                Type.ParseCard -> {
                    map.remove(senderID)
                    val app = box.message(RichMessage::class.java)
                    box.reply(app!!.content)
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