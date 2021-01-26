package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.registerDefaultPermission
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

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
                val inputStream = NetWorkUtil["https://v1.hitokoto.cn/"]?.second ?: return
                inputStreamToResult(inputStream, this)
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
                        val inputStream = NetWorkUtil["https://v1.hitokoto.cn/?c=" + args[0]]?.second ?: return
                        inputStreamToResult(inputStream, this)
                    }
                }
            }
        }
    }

    private suspend fun inputStreamToResult(inputStream: InputStream, sender: CommandSender) {
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        var string: String?
        var text: String? = ""
        while (reader.readLine().also { string = it } != null) {
            text += string
        }
        val `object` = BotData.objectMapper.readTree(text)
        val hitokoto = `object`["hitokoto"].asText()
        val from = `object`["from"].asText()
        sender.sendMessage("『 $hitokoto 』- 「$from」")
    }
}