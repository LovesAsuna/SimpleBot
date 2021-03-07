package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import net.mamoe.mirai.event.events.MessageRecallEvent

object GroupRecallListener : EventListener {
    override fun onAction() {
        Main.eventChannel.subscribeAlways(MessageRecallEvent::class) {
            TODO("撤回消息处理")
        }
    }
}