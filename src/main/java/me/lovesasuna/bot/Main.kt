package me.lovesasuna.bot

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.manager.FileManager
import me.lovesasuna.bot.util.Dependence.Companion.init
import net.mamoe.mirai.console.plugins.PluginBase

/**
 * @author LovesAsuna
 * @date 2020/5/6 21:54
 */
class Main : PluginBase() {
    override fun onEnable() {
        instance = this
        Config.init(this)
        logger.info("正在加载插件依赖！")
        runBlocking {
            init()
        }
        logger.info("插件依赖加载完成！")
        scheduler!!.async {
            FileManager.readValue()
        }
        logger.info("机器人插件启用成功！")
    }

    override fun onDisable() {
        FileManager.writeValue()
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