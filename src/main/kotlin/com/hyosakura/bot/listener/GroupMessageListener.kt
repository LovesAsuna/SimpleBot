package com.hyosakura.bot.listener

import com.hyosakura.bot.Config
import com.hyosakura.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.ClassUtil
import net.mamoe.mirai.event.events.GroupMessageEvent

object GroupMessageListener : EventListener {
    private val listeners = ArrayList<FunctionListener>()

    init {
        val listenersClass = ClassUtil.getClasses(FunctionListener::class.java.typeName.substringBeforeLast("."), com.hyosakura.bot.Main::class.java.classLoader)
        listenersClass.filter {
            !com.hyosakura.bot.Config.DisableFunction.contains(it.simpleName) && ClassUtil.getSuperClass(it)
                .contains(FunctionListener::class.java)
        }.forEach {
            val objectInstance = it.kotlin.objectInstance
            if (objectInstance != null) {
                listeners.add(objectInstance as FunctionListener)
            } else {
                listeners.add(it.getConstructor().newInstance() as FunctionListener)
            }
            com.hyosakura.bot.Main.logger.info("注册拓展功能: ${it.simpleName}")
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun onAction() {
        com.hyosakura.bot.Main.eventChannel.subscribeAlways(GroupMessageEvent::class) {
            listeners.forEach {
                com.hyosakura.bot.Main.scheduler.asyncTask {
                    it.execute(MessageBox(this))
                }
            }
        }
    }
}