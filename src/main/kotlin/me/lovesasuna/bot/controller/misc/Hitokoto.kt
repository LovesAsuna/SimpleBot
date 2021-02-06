package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

object Hitokoto : RawCommand(
    owner = Main,
    primaryName = "一言",
    description = "一言",
    parentPermission = registerDefaultPermission()
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        when (args.size) {
            0 -> {
                // 如果不带参数,默认全部获取
                getResult("https://v1.hitokoto.cn/", this)
            }
            1 -> {
                when (args[0].content) {
                    "help" -> {
                        sendMessage(
                            """
     一言参数: 
     a	Anime - 动画
     b	Comic – 漫画
     c	Game – 游戏
     d	Novel – 小说
     e	Myself – 原创
     f	Internet – 来自网络
     g	Other – 其他
     不填 - 随机
     """.trimIndent()
                        )
                    }
                    else -> {
                        getResult("https://v1.hitokoto.cn/?c=" + args[0], this)
                    }
                }
            }
        }
    }

    private suspend fun getResult(url: String, sender: CommandSender) {
        val `object` = OkHttpUtil.getJson(url)
        val hitokoto = `object`["hitokoto"].asText()
        val from = `object`["from"].asText()
        sender.sendMessage("『 $hitokoto 』- 「$from」")
    }
}