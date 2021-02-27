package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
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
            //todo config
            if (true) {
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