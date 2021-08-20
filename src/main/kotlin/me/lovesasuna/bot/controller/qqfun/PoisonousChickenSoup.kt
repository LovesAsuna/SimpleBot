package me.lovesasuna.bot.controller.qqfun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lovesasuna.bot.controller.FunctionListener
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
        box.reply(
            withContext(Dispatchers.IO) {
                @Suppress("BlockingMethodInNonBlockingContext")
                OkHttpUtil.getJson("https://v2.alapi.cn/api/soul?token=dppfgmdxhKZlt6vB")["data"]["content"].asText()
            }
        )
        return true
    }
}