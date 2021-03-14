package me.lovesasuna.bot.listener

import me.lovesasuna.bot.Config
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.ClassUtil
import net.mamoe.mirai.event.events.GroupMessageEvent

object GroupMessageListener : EventListener {
    private val listeners = ArrayList<FunctionListener>()

    init {
        val listenersClass = ClassUtil.getClasses(FunctionListener::class.java.typeName.substringBeforeLast("."), Main::class.java.classLoader)
        listenersClass.filter {
            !Config.DisableFunction.contains(it.simpleName) && ClassUtil.getSuperClass(it)
                .contains(FunctionListener::class.java)
        }.forEach {
            listeners.add(it.getConstructor().newInstance() as FunctionListener)
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