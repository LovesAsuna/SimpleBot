package me.lovesasuna.bot.controller.qqfun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

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
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle() {
        sendMessage(withContext(Dispatchers.IO) {
            OkHttpUtil.getStr("https://v2.alapi.cn/api/dog?format=text&token=dppfgmdxhKZlt6vB")
        })
    }
}