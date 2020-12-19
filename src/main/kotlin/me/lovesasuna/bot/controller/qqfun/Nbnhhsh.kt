package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.NetWorkUtil

class Nbnhhsh : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text().startsWith("/nbnhhsh ")) {
            val abbreviation = box.text().split(" ")[1]
            val text = BotData.objectMapper.createObjectNode().put("text", abbreviation)
            val post = NetWorkUtil.post(
                "https://lab.magiconch.com/api/nbnhhsh/guess", text.toString().toByteArray(),
                arrayOf("content-type", "application/json")
            )
            val result = post?.second?.bufferedReader()?.lineSequence()?.joinToString()
            if (result != null) {
                box.reply("可能的结果: ${BotData.objectMapper.readTree(result)[0]["trans"] ?: "[]"}")
            }
            return true
        }
        return false
    }
}