package me.lovesasuna.bot.listener

import me.lovesasuna.bot.OriginMain
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.ClassUtil
import me.lovesasuna.bot.util.plugin.Logger
import net.mamoe.mirai.event.events.GroupMessageEvent

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
        OriginMain.bot.eventChannel.subscribeAlways(GroupMessageEvent::class) {
            listeners.forEach {
                OriginMain.scheduler.asyncTask {
                    it.execute(MessageBox(this))
                }
            }
        }
    }
}