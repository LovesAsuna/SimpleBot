package me.lovesasuna.bot

import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.function.Dynamic
import me.lovesasuna.bot.function.Notice
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.manager.FileManager
import me.lovesasuna.bot.util.Dependence.Companion.init
import net.mamoe.mirai.console.plugins.PluginBase
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

/**
 * @author LovesAsuna
 * @date 2020/5/6 21:54
 */
class Main : PluginBase() {
    override fun onEnable() {
        instance = this
        Config.init(this)
        println("[Bot] 机器人插件启用成功！")
        runBlocking {
            init()
            FileManager.readValue()
        }
    }

    override fun onDisable() {
        ObjectOutputStream(FileOutputStream(File(dataFolder.toString() + File.separator + "notice.dat"))).writeObject(Notice.data)
        ObjectOutputStream(FileOutputStream(File(dataFolder.toString() + File.separator + "dynamic.dat"))).writeObject(Dynamic.data)
    }

    companion object {
        lateinit var instance: Main
            private set

        fun initListener() {
            GroupMessageListener.onMessage()
            FriendMessageListener.onMessage()
        }
    }
}