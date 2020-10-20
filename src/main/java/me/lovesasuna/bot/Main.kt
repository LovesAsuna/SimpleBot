package me.lovesasuna.bot

import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.listener.MemberLeaveListener
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.EventListener
import me.lovesasuna.bot.util.plugin.Logger
import me.lovesasuna.bot.util.plugin.PluginScheduler
import net.mamoe.mirai.Bot
import net.mamoe.mirai.join
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
 * @date 2020/7/3 21:54
 */
object Main {
    lateinit var bot: Bot
    lateinit var botConfig: BotConfiguration
    var logger: MiraiLogger? = null
    val scheduler = PluginScheduler()
    val dataFolder = File("${BasicUtil.getLocation(Main.javaClass).path}${File.separator}Bot")
            .also { if (!it.exists()) Files.createDirectories(Paths.get(it.toURI())) }

    private fun initListener() {
        val listenerList = listOf(
                GroupMessageListener, FriendMessageListener, MemberLeaveListener
        )
        listenerList.forEach(EventListener::onAction)
    }

    private fun printSystemInfo() {
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

    @JvmStatic
    suspend fun main(vararg args: String) {
        getLogger("").level = Level.OFF
        printSystemInfo()
        botConfig = BotConfiguration.Default.also {
            it.randomDeviceInfo()
        }
        Config.writeDefault()
        Logger.log("登陆协议: ${botConfig.protocol}", Logger.LogLevel.CONSOLE)
        bot = Bot(Config.data.Account,
                Config.data.Password,
                botConfig
        ).also {
            it.login()
            logger = it.logger
        }

        initListener()

        Runtime.getRuntime().addShutdownHook(Thread {
            Logger.log(Logger.Messages.BOT_SHUTDOWN, Logger.LogLevel.CONSOLE)
        })
        bot.join()
    }
}