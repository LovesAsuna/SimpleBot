package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.registerDefaultPermission
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import java.net.URLEncoder

object Baike : SimpleCommand(
    owner = Main,
    primaryName = "baike",
    description = "百度百科",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(context : String) {
        val url = "https://baike.baidu.com/item/${URLEncoder.encode(context, "UTF-8")}"
        val reader = NetWorkUtil.get(url)!!.second.bufferedReader()
        for (i in 0 until 10) reader.readLine()
        val desc = reader.readLine()
        val args = desc.split("\"")
        if (args.size > 1) {
            sendMessage(args[3].replace(Regex("...$"), ""))
        } else {
            sendMessage("百度百科未收录此词条!")
        }
    }
}