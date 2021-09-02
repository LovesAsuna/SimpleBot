package com.hyosakura.bot.data

import com.hyosakura.bot.util.photo.ImageUtil
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.SingleMessage
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
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

    suspend fun reply(message: Message) = event.subject.sendMessage(message)

    suspend fun reply(plain: String) = event.subject.sendMessage(plain)

    fun isSingleMessage(): Boolean {
        return event.message.size == 1
    }

    suspend fun uploadImage(image: InputStream) = image.uploadAsImage(event.subject)

    suspend fun uploadImage(image: File) = event.subject.uploadImage(image)

    suspend fun uploadImage(image: BufferedImage) = event.subject.uploadImage(ImageUtil.imageToByte(image).toExternalResource())

    fun <M : SingleMessage> message(key: Class<M>): M? {
        event.message.forEach {
            if (key.isInstance(it)) {
                return key.cast(it)
            }
        }
        return null
    }

    fun text() = event.message.content

    fun image() = message(Image::class.java)

    override fun iterator() = event.message.listIterator(1)
}