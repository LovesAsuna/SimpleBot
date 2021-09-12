package com.hyosakura.bot

import com.hyosakura.bot.controller.game.TeamSpeak
import com.hyosakura.bot.listener.EventListener
import com.hyosakura.bot.listener.GroupMessageListener
import com.hyosakura.bot.listener.MemberLeaveListener
import com.hyosakura.bot.service.DBService
import com.hyosakura.bot.util.ClassUtil
import com.hyosakura.bot.util.logger.ContactLogger
import com.hyosakura.bot.util.plugin.PluginScheduler
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import java.lang.management.ManagementFactory
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.jvm.jvmName

/**
 * @author LovesAsuna
 */
object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "com.hyosakura.bot",
        version = "1.0",
        name = "Mirai-Bot"
    )
) {
    val scheduler = PluginScheduler()
    val eventChannel = globalEventChannel()
    override fun onEnable() {
        Logger.getLogger("").level = Level.OFF
        // todo 使用Log4j2接管日志系统
        logger.follower = ContactLogger
        logger.info("[Mirai-Bot] 插件已成功启用!")
        val runtimeMX = ManagementFactory.getRuntimeMXBean()
        val osMX = ManagementFactory.getOperatingSystemMXBean()
        if (runtimeMX != null && osMX != null) {
            val javaInfo = "Java " + runtimeMX.specVersion + " (" + runtimeMX.vmName + " " + runtimeMX.vmVersion + ")"
            val osInfo = "Host: " + osMX.name + " " + osMX.version + " (" + osMX.arch + ")"
            logger.info("System Info: $javaInfo $osInfo")
        } else {
            logger.info("Unable to read system info")
        }
        Config.reload()
        ClassUtil.getClasses("com.hyosakura.bot.controller", Main::class.java.classLoader).forEach {
            val kClass = it.kotlin
            if (!kClass.jvmName.contains("$")) {
                val objectInstance = kClass.objectInstance
                if (objectInstance != null && Command::class.isInstance(objectInstance)) {
                    CommandManager.registerCommand(objectInstance as Command)
                }
            }
        }
        listOf(
            GroupMessageListener, MemberLeaveListener
        ).forEach(EventListener::onAction)
    }

    override fun onDisable() {
        ClassUtil.getClasses("com.hyosakura.bot.service.impl").forEach {
            (it.kotlin.objectInstance as DBService).close()
        }
        TeamSpeak.queries.values.forEach {
            it.exit()
        }
    }
}

object Config : AutoSavePluginConfig("config") {
    val SauceNaoAPI by value<String>()
    val DisableFunction: List<String> by value()
}
