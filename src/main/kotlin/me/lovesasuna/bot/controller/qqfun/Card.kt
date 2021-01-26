package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

object Make : RawCommand(
    owner = Main,
    primaryName = "makecard",
    description = "卡片生成",
    parentPermission = registerDefaultPermission()
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        sendMessage(LightApp(args.content))
    }
}

object Parse : RawCommand(
    owner = Main,
    primaryName = "parsecard",
    description = "卡片解析",
    parentPermission = registerDefaultPermission()
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        sendMessage(args[LightApp]!!.content)
    }
}