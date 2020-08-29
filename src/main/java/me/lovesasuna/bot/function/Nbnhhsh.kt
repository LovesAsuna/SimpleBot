package me.lovesasuna.bot.function

import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

class Nbnhhsh : FunctionListener{
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message.startsWith("/nbnhhsh ")) {
            val abbreviation = message.split(" ")[1]
            val text = BotData.objectMapper!!.createObjectNode().put("text", abbreviation)
            val post = NetWorkUtil.post("https://lab.magiconch.com/api/nbnhhsh/guess", text.toString().toByteArray(),
                    arrayOf("content-type", "application/json"))
            val result = post?.second?.bufferedReader()?.lineSequence()?.joinToString()
            if (result != null) {
                event.reply("可能的结果: ${BotData.objectMapper!!.readTree(result)[0]["trans"] ?: "[]"}")

            }
            return true
        }
        return false
    }
}