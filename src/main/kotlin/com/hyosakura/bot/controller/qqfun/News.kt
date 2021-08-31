package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

object News  : SimpleCommand(
owner = com.hyosakura.bot.Main,
primaryName = "60s",
description = "每日新闻",
parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val url = "https://api.03c3.cn/zb/"
        sendMessage(OkHttpUtil.getIs(OkHttpUtil[url]).run {
            uploadAsImage(getGroupOrNull()!!)
        })

    }
}