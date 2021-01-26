package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import net.mamoe.mirai.event.events.MemberLeaveEvent

object MemberLeaveListener : EventListener {
    override fun onAction() {
        Main.bot.eventChannel.subscribeAlways(MemberLeaveEvent::class) {
            group.sendMessage("刚刚，${member.nameCard}(${member.id})离开了我们！！")
        }
    }

}