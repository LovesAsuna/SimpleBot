package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.IOException

class DeBug : Listener {
    @Throws(IOException::class)
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val groupID = (event as GroupMessageEvent).group.id
        if (groupID == 625924077L || groupID == 2122723273L) {
            if (message.equals("/debug", ignoreCase = true)) {
            }
        }
        return true
    }
}