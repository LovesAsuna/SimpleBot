package me.lovesasuna.bot.data

import me.lovesasuna.bot.util.exceptions.MessageTypeNotSingeException
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream

/**
 * @author LovesAsuna
 **/
class MessageBox(val event: MessageEvent) : Iterable<SingleMessage> {
    var sequence = ArrayList<SingleMessage>()
    val bot = event.bot
    val sender = event.sender
    var group: Group? = null
    private var messageMap = HashMap<Class<out SingleMessage>, ArrayList<SingleMessage>>()

    init {
        if (event is GroupMessageEvent) {
            group = event.group
        }
        val iterator = event.message.iterator()
        while (iterator.hasNext()) {
            val singleMessage = iterator.next()
            sequence.add(singleMessage)
        }
    }

    fun exportMessage(): HashMap<Class<out SingleMessage>, ArrayList<SingleMessage>> {
        //TODO 解决类型匹配问题
        if (messageMap.isEmpty()) {
            forEach {
                if (messageMap[it] == null) {
                    messageMap[it::class.java] = ArrayList<SingleMessage>().apply { this.add(it) }
                }
            }
        }
        return messageMap
    }

    suspend fun reply(message: Message) = event.reply(message)

    suspend fun reply(plain: String) = event.reply(plain)

    fun isSingleMessage(): Boolean {
        return sequence.size == 2
    }

    suspend fun uploadImage(image: InputStream) = event.uploadImage(image)

    suspend fun uploadImage(image: File) = event.uploadImage(image)

    suspend fun uploadImage(image: BufferedImage) = event.uploadImage(image)

    private fun <T : SingleMessage> getFirstMessage(c: Class<T>): T? {
        exportMessage()
        return if (messageMap[c] == null) {
            null
        } else {
            when (messageMap[c]!!.size) {
                0 -> throw IllegalArgumentException()
                1 -> c.cast(messageMap[c]!![0])
                else -> throw MessageTypeNotSingeException(c)
            }
        }
    }

    fun message() = getFirstMessage(PlainText::class.java)?.content ?: ""

    fun image() = getFirstMessage(Image::class.java)

    fun face() = getFirstMessage(Face::class.java)

    fun richMessage() = getFirstMessage(RichMessage::class.java)

    fun at() = getFirstMessage(At::class.java)

    override fun iterator() = sequence.listIterator(1)
}