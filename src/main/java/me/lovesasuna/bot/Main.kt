package me.lovesasuna.bot

import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.manager.FileManager
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.Dependence
import me.lovesasuna.bot.util.plugin.Logger
import me.lovesasuna.bot.util.plugin.PluginScheduler
import net.mamoe.mirai.Bot
import net.mamoe.mirai.join
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiLogger
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author LovesAsuna
 * @date 2020/7/3 21:54
 */
suspend fun main() {
    /*初始化机器人依赖*/
    Dependence.init()

    FileManager.readValue()
    Main.bot = Bot(Config.data.account,
            Config.data.password,
            BotConfiguration.Default.also {
                it.randomDeviceInfo()
            }
    ).also {
        it.login()
        Main.logger = it.logger
    }

    Main.initListener()

    Runtime.getRuntime().addShutdownHook(Thread {
        Logger.log(Logger.Messages.BOT_SHUTDOWN, Logger.LogLevel.CONSOLE)
        FileManager.writeValue()
    })
    Main.bot.join()
}

object Main {
    lateinit var bot: Bot
    var logger: MiraiLogger? = null
    val scheduler = PluginScheduler()
    val dataFolder = File("${BasicUtil.getLocation(Main.javaClass).path}${File.separator}Bot")
            .also { if (!it.exists()) Files.createDirectories(Paths.get(it.toURI())) }

    fun initListener() {
        GroupMessageListener.onMessage()
        FriendMessageListener.onMessage()
    }
}