package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.controller.*
import me.lovesasuna.bot.controller.Danmu.Danmu
import me.lovesasuna.bot.controller.photo.Photo
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.interfaces.EventListener
import me.lovesasuna.bot.util.plugin.Logger
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

object GroupMessageListener : EventListener {
    private val listeners = ArrayList<FunctionListener>()

    init {
        val listenersClass = arrayOf<Class<*>>(
                KeyWord::class.java, McQuery::class.java,
                Bilibili::class.java, Hitokoto::class.java,
                Admin::class.java, DownloadImage::class.java,
                RainbowSix::class.java, RepeatDetect::class.java,
                PictureSearch::class.java, PixivGetter::class.java,
                Danmu::class.java, Photo::class.java, Dynamic::class.java,
                Baike::class.java, Nbnhhsh::class.java, Notice::class.java,
                Card::class.java
        )
        listenersClass.filter {
            !Config.data.DisableFunction.contains(it.simpleName)
        }.forEach {
            listeners.add(it.getConstructor().newInstance() as FunctionListener)
            Logger.log("注册功能: ${it.simpleName}", Logger.LogLevel.INFO)
        }
    }

    override fun onAction() {
        Main.bot.subscribeAlways(GroupMessageEvent::class) {
            listeners.forEach {
                Main.scheduler.asyncTask {
                    it.execute(this, this.message.contentToString(), this.message[Image], this.message[Face])
                }
            }
        }
    }
}