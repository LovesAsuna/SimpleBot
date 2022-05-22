package com.hyosakura.bot.listener

import com.hyosakura.bot.Main
import net.mamoe.mirai.event.events.MessageRecallEvent

object GroupRecallListener : EventListener {
    override fun onAction() {
        Main.eventChannel.subscribeAlways(MessageRecallEvent::class) {
            TODO("撤回消息处理")
        }
    }
}