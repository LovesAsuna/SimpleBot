package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author LovesAsuna
 **/
class WhichTime : FunctionListener {
    val formatter = DateTimeFormatter.ofPattern("HH-mm")

    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "几点了") {
            return false
        }
        val time = formatter.format(LocalDateTime.now())
        box.reply(box.event.uploadImage(NetWorkUtil["https://ty.kuku.me/images/time/$time.jpg"]!!.second))
        return true
    }
}