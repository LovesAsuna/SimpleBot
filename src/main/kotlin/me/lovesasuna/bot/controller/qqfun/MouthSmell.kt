package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

/**
 * @author LovesAsuna
 **/
object MouthSmell : SimpleCommand(
    owner = Main,
    primaryName = "祖安语录",
    "嘴臭",
    description = "祖安语录",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val node = OkHttpUtil.getJson("https://s.nmsl8.club/getloveword?type=2")
        sendMessage(node["content"].asText())
    }
}

object MouthSweat : SimpleCommand(
    owner = Main,
    primaryName = "嘴甜",
    description = "嘴甜",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val node = OkHttpUtil.getJson("https://s.nmsl8.club/getloveword?type=1")
        sendMessage(node["content"].asText())
    }
}