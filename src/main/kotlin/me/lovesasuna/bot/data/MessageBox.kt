package me.lovesasuna.bot.data

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.SingleMessage
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream

/**
 * @author LovesAsuna
 **/
class MessageBox(val event: MessageEvent) : Iterable<SingleMessage> {
    val bot = event.bot
    val sender = event.sender
    var group: Group? = null

    init {
        if (event is GroupMessageEvent) {
            group = event.group
        }
    }

    suspend fun reply(message: Message) = event.reply(message)

    suspend fun reply(plain: String) = event.reply(plain)

    fun isSingleMessage(): Boolean {
        return event.message.size == 1
    }

    suspend fun uploadImage(image: InputStream) = event.uploadImage(image)

    suspend fun uploadImage(image: File) = event.uploadImage(image)

    suspend fun uploadImage(image: BufferedImage) = event.uploadImage(image)


    fun <M : Message> message(key: Message.Key<M>) = event.message[key]

    fun text() = message(PlainText)?.content ?: ""

    fun image() = message(Image)

    override fun iterator() = event.message.listIterator(1)
}