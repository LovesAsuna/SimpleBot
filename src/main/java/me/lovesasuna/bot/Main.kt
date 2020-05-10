package me.lovesasuna.bot

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.listener.FriendMessageListener
import me.lovesasuna.bot.listener.GroupMessageListener
import me.lovesasuna.bot.util.Dependence.Companion.init
import net.mamoe.mirai.console.plugins.PluginBase

/**
 * @author LovesAsuna
 * @date 2020/5/6 21:54
 */
class Main : PluginBase() {
    override fun onEnable() {
        instance = this
        runBlocking {
            init()
        }
    }

    companion object {
        var mapper: ObjectMapper? = null
        lateinit var instance: Main
            private set

        fun initListener() {
            GroupMessageListener.onMessage()
            FriendMessageListener.onMessage()
        }
    }
}