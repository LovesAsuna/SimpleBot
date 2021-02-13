package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil

/**
 * @author LovesAsuna
 **/
class MouthSmell : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        if (message == "/嘴臭" || message == "/祖安语录") {
            val node = OkHttpUtil.getJson("https://s.nmsl8.club/getloveword?type=2")
            box.reply(node["content"].asText())
            return true
        }
        if (message == "/嘴甜") {
            val node = OkHttpUtil.getJson("https://s.nmsl8.club/getloveword?type=1")
            box.reply(node["content"].asText())
            return true
        }
        return true
    }
}