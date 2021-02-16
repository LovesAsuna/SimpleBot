package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil
import java.io.IOException

class Hitokoto : FunctionListener {
    @Throws(IOException::class)
    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        if (message.startsWith("/一言")) {
            val strings = message.split(" ")
            when (strings.size) {
                1 -> {
                    // 如果不带参数,默认全部获取
                    getResult("https://v1.hitokoto.cn/", box)
                }
                2 -> {
                    if ("help".equals(strings[1], ignoreCase = true)) {
                        box.reply(
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
                    } else {
                        getResult("https://v1.hitokoto.cn/?c=" + strings[1], box)
                    }
                }
            }
        }
        return true
    }

    private suspend fun getResult(url: String, box: MessageBox) {
        val `object` = OkHttpUtil.getJson(url)
        val hitokoto = `object`["hitokoto"].asText()
        val from = `object`["from"].asText()
        box.reply("『 $hitokoto 』- 「$from」")
    }
}