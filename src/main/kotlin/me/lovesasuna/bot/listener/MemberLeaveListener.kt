package me.lovesasuna.bot.listener

import me.lovesasuna.bot.OriginMain
import net.mamoe.mirai.event.events.MemberLeaveEvent

object MemberLeaveListener : EventListener {
    override fun onAction() {
        OriginMain.bot.eventChannel.subscribeAlways(MemberLeaveEvent::class) {
            group.sendMessage("刚刚，${member.nameCard}(${member.id})离开了我们！！")
        }
    }

}