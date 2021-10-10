package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.misc.JikiPediaEntity
import com.hyosakura.bot.util.network.OkHttpUtil
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object JikiPedia : SimpleCommand(
    owner = Main,
    primaryName = "查梗",
    description = "查网络流行语",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(str: String) {
        val text = BotData.objectMapper.createObjectNode().put("phrase", str)
            .put("page", 1)
            .put("size", 60)
        val root = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            OkHttpUtil.postJson(
                "https://api.jikipedia.com/go/search_entities",
                text.toString().toRequestBody("application/json".toMediaType())
            )
        }
        for (data in root["data"]) {
            JikiPediaEntity.parse(data)?.let {
                sendMessage(it.toString())
            }
        }
    }
}