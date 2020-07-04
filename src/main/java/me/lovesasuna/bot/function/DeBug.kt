package me.lovesasuna.bot.function

import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.IOException
import java.util.regex.Pattern

class DeBug : Listener {
    private val pattern = Pattern.compile("/debug @")

    @Throws(IOException::class)
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        event as GroupMessageEvent
        val senderID = event.sender.id
        // val groupID = event.group.id
        if (senderID == Config.data.admin) {
            if (message.startsWith("/debug")) {

            }
        }
        return true
    }
}