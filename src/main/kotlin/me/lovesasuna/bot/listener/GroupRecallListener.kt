package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.interfaces.EventListener
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeAlways

object GroupRecallListener : EventListener {
    override fun onAction() {
        Main.bot.subscribeAlways(MessageRecallEvent::class) {
            TODO("撤回消息处理")
        }
    }
}