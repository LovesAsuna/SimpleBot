package me.lovesasuna.bot

import com.google.auto.service.AutoService
import me.lovesasuna.bot.controller.bilibili.Dynamic
import me.lovesasuna.bot.controller.game.RainbowSix
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.listener.EventListener
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.listener.MemberLeaveListener
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.plugin.Logger
import me.lovesasuna.bot.util.plugin.PluginScheduler
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.registeredCommands
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiLogger
import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Level
import java.util.logging.Logger.getLogger

/**
 * @author LovesAsuna
 */


suspend fun main() {
    getLogger("").level = Level.OFF
    OriginMain.printSystemInfo()
    OriginMain.botConfig = BotConfiguration.Default.also {
        it.randomDeviceInfo()
    }
    Config.writeDefault()
    Logger.log("登陆协议: ${OriginMain.botConfig.protocol}", Logger.LogLevel.CONSOLE)
    OriginMain.bot = BotFactory.newBot(
        Config.data.Account,
        Config.data.Password,
        OriginMain.botConfig
    ).also {
        it.login()
        OriginMain.logger = it.logger
    }

    OriginMain.initListener()

    Runtime.getRuntime().addShutdownHook(Thread {
        Logger.log(Logger.Messages.BOT_SHUTDOWN, Logger.LogLevel.CONSOLE)
    })
    OriginMain.bot.join()
}

@AutoService(KotlinPlugin::class)
object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "me.lovesasuna.bot",
        version = "1.0",
        name = "Mirai-Bot"
    )
) {
    override fun onEnable() {
        logger.info("[Mirai-Bot] 插件已成功启用!")
        RainbowSix.register()
        Dynamic.register()
    }
}

object OriginMain {
    lateinit var bot: Bot
    lateinit var botConfig: BotConfiguration
    var logger: MiraiLogger? = null
    val scheduler = PluginScheduler()
    val dataFolder = File("${BasicUtil.getLocation(OriginMain.javaClass).path}${File.separator}Bot")
        .also { if (!it.exists()) Files.createDirectories(Paths.get(it.toURI())) }

    fun initListener() {
        val listenerList = listOf(
            GroupMessageListener, FriendMessageListener, MemberLeaveListener
        )
        listenerList.forEach(EventListener::onAction)
    }

    fun printSystemInfo() {
        val runtimeMX = ManagementFactory.getRuntimeMXBean()
        val osMX = ManagementFactory.getOperatingSystemMXBean()
        if (runtimeMX != null && osMX != null) {
            val javaInfo = "Java " + runtimeMX.specVersion + " (" + runtimeMX.vmName + " " + runtimeMX.vmVersion + ")"
            val osInfo = "Host: " + osMX.name + " " + osMX.version + " (" + osMX.arch + ")"
            println("System Info: $javaInfo $osInfo")
        } else {
            println("Unable to read system info")
        }
    }
}