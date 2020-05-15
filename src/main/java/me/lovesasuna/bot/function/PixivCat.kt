package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.Listener
import me.lovesasuna.bot.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

class PixivCat : Listener{
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        when {
            message.startsWith("/pixiv work ") -> {
                val ID = BasicUtil.ExtraceInt(message.split(" ")[2])
                event.reply(event.uploadImage(NetWorkUtil.fetch("https://pixiv.cat/$ID.jpg")!!.first))
            }
            message.contains("i.pximg.net") -> {
                event.reply(event.uploadImage(NetWorkUtil.fetch(message.replace("i.pximg.net", "/i.pixiv.cat"))!!.first))
            }
        }
        return true
    }


}