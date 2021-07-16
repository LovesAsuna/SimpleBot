package me.lovesasuna.bot.controller.qqfun

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.photo.ImageUtil
import me.lovesasuna.bot.util.photo.gif.AnimatedGifEncoder
import me.lovesasuna.bot.util.photo.gif.GifDecoder
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * @author LovesAsuna
 */
class RepeatDetect : FunctionListener {
    private val maps: MutableMap<Long, MutableList<MessageChain>> = HashMap()
    private val gifDecoder = GifDecoder()
    private val gifEncoder = AnimatedGifEncoder()
    private val random = Random()

    override suspend fun execute(box: MessageBox): Boolean {
        val groupID = box.group!!.id
        maps.putIfAbsent(groupID, ArrayList())
        val messageList = maps[groupID]!!

        if (messageList.size >= 3) {
            messageList.removeAt(0)
        }

        operate(box.event, messageList)

        if (messageList.size < 3) {
            return false
        }

        if (isRepeat(messageList)) {
            Main.scheduler.asyncTask {

                val messageChain = box.event.message
                box.reply(buildMessageChain {
                    if (messageChain.size >= 2) {
                        when (messageChain.size) {
                            2 -> {
                                when (messageChain[1]) {
                                    is PlainText -> {
                                        +randomText(box, messageChain[1] as PlainText)
                                    }
                                    is Image -> {

                                    }
                                    else -> box.reply(messageList[2])
                                }
                            }
                            else -> {
                                messageChain.shuffled().forEach {
                                    when (it) {
                                        is PlainText -> {
                                            +randomText(box, it)
                                        }
                                        else -> {
                                            +it
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
                messageList.clear()
                this
            }
        }
        return true
    }

    private fun randomText(box : MessageBox, text: PlainText): MessageChain {
        return buildMessageChain {
            if (random.nextBoolean()) {
                mutableListOf(text.content.split("")).apply {
                    this.shuffle()
                    joinToString("")
                }
                ArrayList<Char>().apply {
                    text.content.forEach { c ->
                        this.add(c)
                    }
                    this.shuffle()
                    val builder = StringBuilder()
                    this.forEach { c ->
                        builder.append(c)
                    }
                    +builder.toString()
                }
            } else {
                +text
            }
        }
    }

    private suspend fun randomImage(box : MessageBox, image: Image): MessageChain {
        val url = image.queryUrl()
        val cloneInputStream = OkHttpUtil.inputStreamClone(OkHttpUtil.getIs(OkHttpUtil[url]))!!
        return buildMessageChain {
            when (ImageUtil.getImageType(ByteArrayInputStream(cloneInputStream.toByteArray()))) {
                "gif" -> {
                    val type = random.nextInt(4)
                    val out = ByteArrayOutputStream()
                    try {
                        gifDecoder.read(ByteArrayInputStream(cloneInputStream.toByteArray()))
                        gifEncoder.start(out)
                        gifEncoder.setDelay(gifDecoder.getDelay(0))
                        gifDecoder.frames.forEach {
                            gifEncoder.addFrame(getOperatedImageByType(it, type))
                        }
                        gifEncoder.finish()
                    } catch (e: Exception) {
                        +"发生未知错误: ${e.javaClass}"
                        +e.message!!.run {
                            "堆栈信息: ${BasicUtil.debug(this)}"
                        }
                    }
                    +box.uploadImage(ByteArrayInputStream(out.toByteArray()))
                }
                else -> {
                    val bufferedImage = getOperatedImage(ImageIO.read(ByteArrayInputStream(cloneInputStream.toByteArray())))
                    +box.uploadImage(bufferedImage)
                }
            }
        }
    }

    /**
     * @param image 图片
     * @param type 指定的处理类型
     * @return 返回随机处理后的图片
     */
    private fun getOperatedImageByType(image: BufferedImage, type: Int): BufferedImage {
        return image.let {
            when (type) {
                0 -> ImageUtil.rotateImage(it, 180)
                1 -> ImageUtil.mirrorImage(it)
                2 -> ImageUtil.reverseImage(it, 1)
                3 -> ImageUtil.reverseImage(it, 2)
                else -> it
            }
        }
    }


    /**
     * @param image 图片
     * @return 返回随机处理后的图片
     */
    private fun getOperatedImage(image: BufferedImage) = getOperatedImageByType(image, random.nextInt(4))

    private fun operate(event: MessageEvent, messageList: MutableList<MessageChain>) {
        messageList.add(event.message)
    }

    private fun isRepeat(messageList: MutableList<MessageChain>): Boolean {
        val first = messageList.first()
        val second = messageList[1]
        val third = messageList[2]
        if (first.contentEquals(second, strict = true) && second.contentEquals(third, strict = true)) {
            return true
        }
        return false
    }
}