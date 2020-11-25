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
class PoisonousChickenSoup : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message != "/毒鸡汤") {
            return false
        }
        val response = NetWorkUtil["https://v1.alapi.cn/api/soul"]
        event.reply(
            if (response!!.first == 200) {
                BotData.objectMapper.readTree(response.second)["data"]["title"].asText()
            } else {
                "获取失败！"
            }
        )
        return true
    }
}