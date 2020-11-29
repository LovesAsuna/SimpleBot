package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author LovesAsuna
 **/
class WhichTime : FunctionListener {
    val formatter = DateTimeFormatter.ofPattern("HH-mm")

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        if (message != "几点了") {
            return false
        }
        val time = formatter.format(LocalDateTime.now())
        event.reply(event.uploadImage(NetWorkUtil["https://ty.kuku.me/images/time/$time.jpg"]!!.second))
        return true
    }
}