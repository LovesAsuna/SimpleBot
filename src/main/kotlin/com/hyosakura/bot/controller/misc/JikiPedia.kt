package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.misc.JikiPediaEntity
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.network.Request.toJson
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

object JikiPedia : SimpleCommand(
    owner = Main,
    primaryName = "查梗",
    description = "查网络流行语",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(str: String) {
        val text = BotData.objectMapper.createObjectNode().put("phrase", str)
        val root = Request.postJson(
            "https://api.jikipedia.com/go/auto_complete",
            text,
            10000,
            mapOf("Client" to "Web")
        ).toJson()
        for (data in root["data"]) {
            JikiPediaEntity.parse(data)?.let {
                sendMessage(it.toString())
            }
        }
    }
}