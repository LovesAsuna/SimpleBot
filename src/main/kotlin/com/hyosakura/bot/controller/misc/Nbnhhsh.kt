package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.network.Request.toJson
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

object Nbnhhsh : SimpleCommand(
    owner = Main,
    primaryName = "nbnhhsh",
    description = "能不能好好说话?",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(abbreviation: String) {
        val text = BotData.objectMapper.createObjectNode().put("text", abbreviation)
        sendMessage(
            "可能的结果: ${
                withContext(Dispatchers.IO) {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    Request.postJson(
                        "https://lab.magiconch.com/api/nbnhhsh/guess",
                        text
                    ).toJson()[0]["trans"] ?: "[]"
                }
            }"
        )
    }
}