package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.registerDefaultPermission
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

/**
 * @author LovesAsuna
 **/
object DogLicking : SimpleCommand(
    owner = Main,
    primaryName = "/舔狗日记",
    description = "舔狗日记",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val node = BotData.objectMapper.readTree(NetWorkUtil["http://api.yyhy.me/tg.php?type=api"]!!.second)
        sendMessage(
            if (node["code"].asInt() == 1) {
                node["date"].asText() + "\n" + node["content"].asText()
            } else {
                "获取失败！！"
            }
        )
    }
}