package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author LovesAsuna
 **/
class DogLicking : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "/舔狗日记") {
            return false
        }
        val reader = BufferedReader(InputStreamReader(NetWorkUtil["https://v1.alapi.cn/api/dog?format=text"]!!.second))
        box.reply(reader.readLine())
        return true
    }
}