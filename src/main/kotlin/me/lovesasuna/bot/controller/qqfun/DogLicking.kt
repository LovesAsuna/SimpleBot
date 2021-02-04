package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.registerDefaultPermission
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author LovesAsuna
 **/
object DogLicking : SimpleCommand(
    owner = Main,
    primaryName = "舔狗日记",
    description = "舔狗日记",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val reader = BufferedReader(InputStreamReader(NetWorkUtil["https://v1.alapi.cn/api/dog?format=text"]!!.second, "UTF-8"))
        sendMessage(reader.readLine())
    }
}