package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.file.Config
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.events.FriendMessageEvent

object FriendMessageListener : EventListener {
    private val listeners: MutableList<FunctionListener> = ArrayList()

    init {
        val listenersClass = arrayOf<Class<*>>(

        )

        listenersClass.forEach { c -> listeners.add(c.getConstructor().newInstance() as FunctionListener) }
    }

    override fun onAction() {
        Main.bot.eventChannel.subscribeAlways(FriendMessageEvent::class) {
            val senderID = sender.id
            if (Config.data.Admin.contains(senderID)) {
                return@subscribeAlways
            }
            listeners.forEach {
                Main.scheduler.asyncTask {
                    it.execute(MessageBox(this))
                }
            }
        }
    }
}