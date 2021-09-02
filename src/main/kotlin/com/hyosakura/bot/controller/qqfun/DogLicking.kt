package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.OkHttpUtil
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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