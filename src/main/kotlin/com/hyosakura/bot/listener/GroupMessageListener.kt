package com.hyosakura.bot.listener

import com.hyosakura.bot.Config
import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.util.ClassUtil
import net.mamoe.mirai.event.events.GroupMessageEvent

object GroupMessageListener : EventListener {
    private val listeners = ArrayList<FunctionListener>()

    init {
        val listenersClass = ClassUtil.getClasses(FunctionListener::class.java.typeName.substringBeforeLast("."), Main::class.java.classLoader)
        listenersClass.filter {
            !Config.DisableFunction.contains(it.simpleName) && ClassUtil.getSuperClass(it)
                .contains(FunctionListener::class.java)
        }.forEach {
            val objectInstance = it.kotlin.objectInstance
            if (objectInstance != null) {
                listeners.add(objectInstance as FunctionListener)
            } else {
                listeners.add(it.getConstructor().newInstance() as FunctionListener)
            }
            Main.logger.info("注册拓展功能: ${it.simpleName}")
        }
    }

    override fun onAction() {
        Main.eventChannel.subscribeAlways(GroupMessageEvent::class) {
            listeners.forEach {
                Main.scheduler.asyncTask {
                    it.execute(MessageBox(this))
                }
            }
        }
    }
}