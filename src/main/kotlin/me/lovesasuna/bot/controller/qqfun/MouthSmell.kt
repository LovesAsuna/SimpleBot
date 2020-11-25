package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

/**
 * @author LovesAsuna
 **/
class MouthSmell : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message == "/嘴臭" || message == "/祖安语录") {
            val node = BotData.objectMapper.readTree(NetWorkUtil["https://s.nmsl8.club/getloveword?type=2"]!!.second)
            event.reply(node["content"].asText())
            return true
        }
        if (message == "/嘴甜") {
            val node = BotData.objectMapper.readTree(NetWorkUtil["https://s.nmsl8.club/getloveword?type=1"]!!.second)
            event.reply(node["content"].asText())
            return true
        }
        return true
    }
}