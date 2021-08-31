package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.Main
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

object Make : RawCommand(
    owner = com.hyosakura.bot.Main,
    primaryName = "makecard",
    description = "卡片生成",
    parentPermission = registerDefaultPermission()
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        sendMessage(LightApp(args.content))
    }
}

object Parse : RawCommand(
    owner = com.hyosakura.bot.Main,
    primaryName = "parsecard",
    description = "卡片解析",
    parentPermission = registerDefaultPermission()
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        sendMessage(args[LightApp]!!.content)
    }
}