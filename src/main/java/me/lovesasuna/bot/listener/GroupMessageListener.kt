package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.*
import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

class GroupMessageListener {
    private val listeners: MutableList<Listener> = ArrayList()

    init {
        val listenersClass = arrayOf<Class<*>>(
                Misc::class.java, McQuery::class.java,
                Bilibili::class.java, Hitokoto::class.java,
                DeBug::class.java, DownloadImage::class.java,
                RainbowSix::class.java, RepeatDetect::class.java
        )

        listenersClass.forEach { c -> listeners.add(c.getConstructor().newInstance() as Listener) }
    }


    companion object {
        val listener = GroupMessageListener()
        fun onMessage() {
            Main.instance.subscribeGroupMessages {
                always {
                    listener.listeners.forEach { listener -> listener.execute(this, this.message.contentToString(), this.message[Image], this.message[Face]) }
                }
            }
        }
    }

}