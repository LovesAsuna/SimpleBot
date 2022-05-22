package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.util.network.Request

/**
 * @author LovesAsuna
 **/
class PoisonousChickenSoup : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "/毒鸡汤") {
            return false
        }
        box.reply(
            Request.getJson("https://v2.alapi.cn/api/soul?token=dppfgmdxhKZlt6vB")["data"]["content"].asText()
        )
        return true
    }
}