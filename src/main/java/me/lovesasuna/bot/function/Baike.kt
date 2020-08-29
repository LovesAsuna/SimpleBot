package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.net.URLEncoder

class Baike : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message.startsWith("/baike ")) {
            val string = message.split(" ")[1]
            val url = "https://baike.baidu.com/item/${URLEncoder.encode(string, "UTF-8")}"
            val reader = NetWorkUtil.get(url)!!.second.bufferedReader()
            for (i in 0 until 10) reader.readLine()
            val desc = reader.readLine()
            val args = desc.split("\"")
            if (args.size > 1) {
                event.reply(args[3].replace(Regex("...$"), ""))
            } else {
                event.reply("百度百科未收录此词条!")
            }
            return true
        }
        return false

    }

}