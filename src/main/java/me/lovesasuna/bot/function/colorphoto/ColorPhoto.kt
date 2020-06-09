package me.lovesasuna.bot.function.colorphoto

import me.lovesasuna.bot.util.Listener
import me.lovesasuna.bot.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import java.net.URL

class ColorPhoto : Listener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        var source : Source?
        if (message.startsWith("/色图")) {
            when (message.split(" ")[1]) {
                "pixiv" -> {
                    source = Pixiv()
                    val data = source.fetchData()
                    val url = data?.split("|")?.get(0)
                    val quota = data?.split("|")?.get(1)
                    event.reply(event.uploadImage(NetWorkUtil.fetch(url)!!.first) + PlainText("\n剩余次数: $quota"))
                }
                "random" -> {
                    source = Random()
                    event.reply(event.uploadImage(NetWorkUtil.fetch(source.fetchData())!!.first))
                }
            }
        }
        return true
    }

}