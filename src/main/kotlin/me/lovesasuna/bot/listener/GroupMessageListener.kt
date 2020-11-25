package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.ClassUtil
import me.lovesasuna.bot.util.interfaces.EventListener
import me.lovesasuna.bot.util.plugin.Logger
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

object GroupMessageListener : EventListener {
    private val listeners = ArrayList<FunctionListener>()

    init {
        val listenersClass = ClassUtil.getClasses(FunctionListener::class.java.typeName.substringBeforeLast("."))
        listenersClass.filter {
            !Config.data.DisableFunction.contains(it.simpleName) && ClassUtil.getSuperClass(it)
                .contains(FunctionListener::class.java)
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