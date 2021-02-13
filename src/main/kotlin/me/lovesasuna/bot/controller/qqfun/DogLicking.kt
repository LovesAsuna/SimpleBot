package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil

/**
 * @author LovesAsuna
 **/
class DogLicking : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "/舔狗日记") {
            return false
        }
        box.reply(OkHttpUtil.getStr("https://v1.alapi.cn/api/dog?format=text"))
        return true
    }
}