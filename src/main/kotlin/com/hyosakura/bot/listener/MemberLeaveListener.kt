package com.hyosakura.bot.listener

import com.hyosakura.bot.Main
import net.mamoe.mirai.event.events.MemberLeaveEvent

object MemberLeaveListener : EventListener {
    override fun onAction() {
        Main.eventChannel.subscribeAlways(MemberLeaveEvent::class) {
            group.sendMessage("刚刚，${member.nick}(${member.id})离开了我们！！")
        }
    }

}