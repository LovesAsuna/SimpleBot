package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.NetWorkUtil

/**
 * @author LovesAsuna
 **/
class MouthSmell : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        if (message == "/嘴臭" || message == "/祖安语录") {
            val node = BotData.objectMapper.readTree(NetWorkUtil["https://s.nmsl8.club/getloveword?type=2"]!!.second)
            box.reply(node["content"].asText())
            return true
        }
        if (message == "/嘴甜") {
            val node = BotData.objectMapper.readTree(NetWorkUtil["https://s.nmsl8.club/getloveword?type=1"]!!.second)
            box.reply(node["content"].asText())
            return true
        }
        return true
    }
}