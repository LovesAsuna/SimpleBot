package com.hyosakura.bot.controller.misc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.hyosakura.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import java.net.URLEncoder

object Baike : SimpleCommand(
    owner = com.hyosakura.bot.Main,
    primaryName = "baike",
    description = "百度百科",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle(context : String) {
        val url = "https://baike.baidu.com/item/${URLEncoder.encode(context, "UTF-8")}"
        val reader = OkHttpUtil.getIs(OkHttpUtil[url]).bufferedReader()
        val desc = withContext(Dispatchers.IO) {
            for (i in 0 until 10) reader.readLine()
            reader.readLine()
        }
        val args = desc.split("\"")
        if (args.size > 1) {
            sendMessage(args[3].replace(Regex("...$"), ""))
        } else {
            sendMessage("百度百科未收录此词条!")
        }
    }
}