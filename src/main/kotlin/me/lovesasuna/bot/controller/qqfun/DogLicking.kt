package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.NetWorkUtil


/**
 * @author LovesAsuna
 **/
class DogLicking : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "/舔狗日记") {
            return false
        }
        val node = BotData.objectMapper.readTree(NetWorkUtil["http://api.yyhy.me/tg.php?type=api"]!!.second)
        box.reply(
            if (node["code"].asInt() == 1) {
                node["date"].asText() + "\n" + node["content"].asText()
            } else {
                "获取失败！！"
            }
        )
        return true
    }
}