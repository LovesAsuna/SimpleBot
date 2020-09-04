package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.interfaces.EventListener
import net.mamoe.mirai.event.events.MemberLeaveEvent
import net.mamoe.mirai.event.subscribeAlways

object MemberLeaveListener : EventListener{
    override fun onAction() {
        Main.bot.subscribeAlways(MemberLeaveEvent::class) {
            group.sendMessage("刚刚，${member.nameCard}离开了我们！！")
        }
    }

}