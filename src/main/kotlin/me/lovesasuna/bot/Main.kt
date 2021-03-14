package me.lovesasuna.bot

import com.google.auto.service.AutoService
import me.lovesasuna.bot.listener.EventListener
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.listener.MemberLeaveListener
import me.lovesasuna.bot.util.ClassUtil
import me.lovesasuna.bot.util.plugin.PluginScheduler
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
@AutoService(KotlinPlugin::class)
object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "me.lovesasuna.bot",
        version = "1.0",
        name = "Mirai-Bot"
    )
) {
    val scheduler = PluginScheduler()
    val eventChannel = globalEventChannel()
    override fun onEnable() {
        Logger.getLogger("").level = Level.OFF
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
        ClassUtil.getClasses("me.lovesasuna.bot.controller", Main::class.java.classLoader).forEach {
            val kClass = it.kotlin
            if (!kClass.jvmName.contains("$")) {
                val objectInstance = kClass.objectInstance
                if (objectInstance != null && Command::class.isInstance(objectInstance)) {
                    CommandManager.registerCommand(objectInstance as Command)
                }
            }
        }
        listOf(
            GroupMessageListener, FriendMessageListener, MemberLeaveListener
        ).forEach(EventListener::onAction)
    }
}

object Config : AutoSavePluginConfig("config") {
    val LoliconAPI by value<String>()
    val SauceNaoAPI by value<String>()
    val DisableFunction: List<String> by value()
}
