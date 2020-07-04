package me.lovesasuna.bot.listener

import kotlinx.coroutines.async
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.*
import me.lovesasuna.bot.function.Danmu.Danmu
import me.lovesasuna.bot.function.colorphoto.ColorPhoto
import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import kotlin.system.measureTimeMillis

class GroupMessageListener {
    val listeners = ArrayList<Listener>()

    init {
        val listenersClass = arrayOf<Class<*>>(
                Misc::class.java, McQuery::class.java,
                Bilibili::class.java, Hitokoto::class.java,
                DeBug::class.java, DownloadImage::class.java,
                RainbowSix::class.java, RepeatDetect::class.java,
                PictureSearch::class.java, PixivCat::class.java,
                Notice::class.java, Danmu::class.java, ColorPhoto::class.java,
                Dynamic::class.java
        )
        listenersClass.forEach { listeners.add(it.getConstructor().newInstance() as Listener) }
    }


    companion object {
        val listener = GroupMessageListener()
        fun onMessage() {
            Main.bot.subscribeGroupMessages {
                always {
                    listener.listeners.forEach {
                        Main.scheduler.async {
                            it.execute(this@always, this@always.message.contentToString(), this@always.message[Image], this@always.message[Face])
                        }
                    }
                }
            }
        }
    }

}