package com.hyosakura.bot.controller.qqfun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.hyosakura.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object Nbnhhsh : SimpleCommand(
    owner = com.hyosakura.bot.Main,
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
                    OkHttpUtil.postJson(
                        "https://lab.magiconch.com/api/nbnhhsh/guess",
                        text.toString().toRequestBody("application/json".toMediaType())
                    )[0]["trans"] ?: "[]"
                }
            }"
        )
    }
}