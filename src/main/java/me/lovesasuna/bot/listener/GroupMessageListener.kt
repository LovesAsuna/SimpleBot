package me.lovesasuna.bot.listener

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.*
import me.lovesasuna.bot.function.Danmu.Danmu
import me.lovesasuna.bot.function.colorphoto.ColorPhoto
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
                RainbowSix::class.java, RepeatDetect::class.java,
                PictureSearch::class.java, PixivCat::class.java,
                Notice::class.java, Danmu::class.java, ColorPhoto::class.java
        )

        listenersClass.forEach { c -> listeners.add(c.getConstructor().newInstance() as Listener) }
    }


    companion object {
        val listener = GroupMessageListener()
        fun onMessage() {
            Main.instance.subscribeGroupMessages {
                always {
                    listener.listeners.forEach {
                        GlobalScope.async {
                            it.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
                        }
                    }
                }
            }
        }
    }

}