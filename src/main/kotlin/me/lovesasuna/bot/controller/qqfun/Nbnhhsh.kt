package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

object Nbnhhsh : SimpleCommand(
    owner = Main,
    primaryName = "nbnhhsh",
    description = "能不能好好说话？",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(abbreviation: String) {
        val text = me.lovesasuna.bot.data.BotData.objectMapper.createObjectNode().put("text", abbreviation)
        val post = me.lovesasuna.lanzou.util.NetWorkUtil.post(
            "https://lab.magiconch.com/api/nbnhhsh/guess", text.toString().toByteArray(),
            arrayOf("content-type", "application/json")
        )
        val result = post?.second?.bufferedReader()?.lineSequence()?.joinToString()
        if (result != null) {
            sendMessage("可能的结果: ${me.lovesasuna.bot.data.BotData.objectMapper.readTree(result)[0]["trans"] ?: "[]"}")
        }
    }
}