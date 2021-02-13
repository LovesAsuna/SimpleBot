package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.network.OkHttpUtil
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
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
        box.reply(OkHttpUtil.getIs(OkHttpUtil["https://ty.kuku.me/images/time/$time.jpg"]).uploadAsImage(box.group!!))
        return true
    }
}