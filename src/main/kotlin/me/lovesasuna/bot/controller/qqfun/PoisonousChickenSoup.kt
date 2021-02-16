package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil

/**
 * @author LovesAsuna
 **/
class PoisonousChickenSoup : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "/毒鸡汤") {
            return false
        }
        val response = OkHttpUtil["https://v1.alapi.cn/api/soul"]
        box.reply(
            if (response.code == 200) {
                BotData.objectMapper.readTree(response.body!!.byteStream())["data"]["title"].asText()
            } else {
                "获取失败！"
            }
        )
        return true
    }
}