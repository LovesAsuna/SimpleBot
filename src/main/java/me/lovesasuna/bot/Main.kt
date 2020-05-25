package me.lovesasuna.bot

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.function.Notice
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.util.Dependence.Companion.init
import me.lovesasuna.bot.util.SRVConvertUtil
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.message.data.MessageChain
import java.io.File

/**
 * @author LovesAsuna
 * @date 2020/5/6 21:54
 */
class Main : PluginBase() {
    private lateinit var bot: Bot


    @Suppress("UNCHECKED_CAST")
    override fun onEnable() {
        instance = this
        Config.init(this)

        runBlocking {
            init()
            if (File(dataFolder.toString() + File.separator + "notice.json").exists()) {
                BotData.objectMapper?.readValue(File(dataFolder.toString() + "notice.json"), ArrayList::class.java)?.forEach {
                    it as Triple<Long, Long, MessageChain>
                    Notice.msgList.add(it)
                }
            }
        }
    }

    override fun onDisable() {
        BotData.objectMapper?.writeValue(File(dataFolder.toString() + File.separator + "notice.json"), Notice.msgList)
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