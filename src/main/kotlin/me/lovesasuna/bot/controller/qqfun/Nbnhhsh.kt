package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class Nbnhhsh : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text().startsWith("/nbnhhsh ")) {
            val abbreviation = box.text().split(" ")[1]
            val text = BotData.objectMapper.createObjectNode().put("text", abbreviation)
            box.reply(
                "可能的结果: ${
                    OkHttpUtil.postJson(
                        "https://lab.magiconch.com/api/nbnhhsh/guess",
                        text.toString().toRequestBody("application/json".toMediaType())
                    )[0]["trans"] ?: "[]"
                }"
            )
            return true
        }
        return false
    }
}