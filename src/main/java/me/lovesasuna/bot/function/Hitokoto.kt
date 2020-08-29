package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.jvm.Throws

class Hitokoto : FunctionListener {
    @Throws(IOException::class)
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message.startsWith("/一言")) {
            var reader: BufferedReader
            val strings = message.split(" ").toTypedArray()
            val mapper = ObjectMapper()
            /*如果不带参数,默认全部获取*/
            if (strings.size == 1) {
                val inputStream = NetWorkUtil.get("https://v1.hitokoto.cn/")?.second ?: return false
                reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                var string: String?
                var text: String? = ""
                while (reader.readLine().also { string = it } != null) {
                    text += string
                }
                val `object` = mapper.readTree(text)
                val hitokoto = `object`["hitokoto"].asText()
                val from = `object`["from"].asText()
                event.reply("『 $hitokoto 』- 「$from」")
            }
            /*如果长度为2*/
            if (strings.size == 2) {
                if ("help".equals(strings[1], ignoreCase = true)) {
                    event.reply("""
     一言参数: 
     a	Anime - 动画
     b	Comic – 漫画
     c	Game – 游戏
     d	Novel – 小说
     e	Myself – 原创
     f	Internet – 来自网络
     g	Other – 其他
     不填 - 随机
     """.trimIndent())
                } else {
                    val inputStream = NetWorkUtil.get("https://v1.hitokoto.cn/?c=" + strings[1])?.second ?: return false
                    reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    var string: String?
                    var text: String? = ""
                    while (reader.readLine().also { string = it } != null) {
                        text += string
                    }
                    val `object` = mapper.readTree(text)
                    val hitokoto = `object`["hitokoto"].asText()
                    val from = `object`["from"].asText()
                   event.reply("『 $hitokoto 』- 「$from」")
                }
            }
        }
        return true
    }
}