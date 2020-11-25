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
class DogLicking : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message != "/舔狗日记") {
            return false
        }
        val node = BotData.objectMapper.readTree(NetWorkUtil["http://api.yyhy.me/tg.php?type=api"]!!.second)
        event.reply(
            if (node["code"].asInt() == 1) {
                node["date"].asText() + "\n" + node["content"].asText()
            } else {
                "获取失败！！"
            }
        )
        return true
    }
}