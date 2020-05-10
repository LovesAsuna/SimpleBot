package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.Sort
import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.message.data.Image

class FriendMessageListener {
    private val listeners: MutableList<Listener> = ArrayList()

    init {
        val listenersClass = arrayOf<Class<*>>(
               Sort::class.java
        )

        listenersClass.forEach { c -> listeners.add(c.getConstructor().newInstance() as Listener) }
    }

    companion object {
        val listener = FriendMessageListener()
        fun onMessage() {
            Main.instance.subscribeFriendMessages {
                always {
                    val senderID = sender.id
                    if (senderID != 625924077L) {
                        return@always
                    }
                    val message = message.contentToString()
                    val image = this.message[Image]
                    listener.listeners.forEach { listener -> listener.execute(this, message, image, null) }
                }
            }
        }
    }
}