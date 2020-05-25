package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf
import java.io.IOException
import java.util.regex.Pattern

class DeBug : Listener {
    private val pattern = Pattern.compile("/debug @")

    @Throws(IOException::class)
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val senderID = (event as GroupMessageEvent).sender.id
        if (senderID == 625924077L || senderID == 2122723273L) {
            if (message.startsWith("/debug ")) {
                var messageChain = messageChainOf(PlainText(event.message[3].contentToString().replaceFirst(" ", "")))
                event.message.listIterator(4).forEach {
                    messageChain += it
                }
                event.reply(messageChain)
            }
        }
        return true
    }
}