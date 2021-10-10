package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.util.BasicUtil
import com.hyosakura.bot.util.image.ImageUtil
import com.hyosakura.bot.util.image.gif.AnimatedGifEncoder
import com.hyosakura.bot.util.image.gif.GifDecoder
import com.hyosakura.bot.util.network.OkHttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private val textPattern = "[\\u4e00-\\u9fa5_a-zA-Z0-9]|" +
            "[\\uD83C\\uDF00-\\uD83D\\uDDFF]|" +
            "[\\uD83E\\uDD00-\\uD83E\\uDDFF]|" +
            "[\\uD83D\\uDE00-\\uD83D\\uDE4F]|" +
            "[\\uD83D\\uDE80-\\uD83D\\uDEFF]|" +
            "[\\u2600-\\u26FF]\\uFE0F?|" +
            "[\\u2700-\\u27BF]\\uFE0F?|" +
            "\\u24C2\\uFE0F?|" +
            "[\\uD83C\\uDDE6-\\uD83C\\uDDFF]{1,2}|" +
            "[\\uD83C\\uDD70\\uD83C\\uDD71\\uD83C\\uDD7E\\uD83C\\uDD7F\\uD83C\\uDD8E\\uD83C\\uDD91-\\uD83C\\uDD9A]\\uFE0F?|" +
            "[\\u0023\\u002A\\u0030-\\u0039]\\uFE0F?\\u20E3|" +
            "[\\u2194-\\u2199\\u21A9-\\u21AA]\\uFE0F?|" +
            "[\\u2B05-\\u2B07\\u2B1B\\u2B1C\\u2B50\\u2B55]\\uFE0F?|" +
            "[\\u2934\\u2935]\\uFE0F?|" +
            "[\\u3030\\u303D]\\uFE0F?|" +
            "[\\u3297\\u3299]\\uFE0F?|" +
            "[\\uD83C\\uDE01\\uD83C\\uDE02\\uD83C\\uDE1A\\uD83C\\uDE2F\\uD83C\\uDE32-\\uD83C\\uDE3A\\uD83C\\uDE50\\uD83C\\uDE51]\\uFE0F?|" +
            "[\\u203C\\u2049]\\uFE0F?|" +
            "[\\u25AA\\u25AB\\u25B6\\u25C0\\u25FB-\\u25FE]\\uFE0F?|" +
            "[\\u00A9\\u00AE]\\uFE0F?|" +
            "[\\u2122\\u2139]\\uFE0F?|" +
            "\\uD83C\\uDC04\\uFE0F?|" +
            "\\uD83C\\uDCCF\\uFE0F?|" +
            "[\\u231A\\u231B\\u2328\\u23CF\\u23E9-\\u23F3\\u23F8-\\u23FA]\\uFE0F?"

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
                                        +randomText(messageChain[1] as PlainText)
                                    }
                                    is Image -> {
                                        +randomImage(box, messageChain[1] as Image)
                                    }
                                    else -> box.reply(messageList[2])
                                }
                            }
                            else -> {
                                messageChain.shuffled().forEach {
                                    when (it) {
                                        is PlainText -> {
                                            +randomText(it)
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

    private fun randomText(text: PlainText): MessageChain {
        return buildMessageChain {
            if (random.nextBoolean()) {
                ArrayList<String>().apply {
                    Regex(textPattern).findAll(text.content).forEach {
                        this.add(it.value)
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

    private suspend fun randomImage(box: MessageBox, image: Image): MessageChain {
        val url = image.queryUrl()
        val cloneInputStream = OkHttpUtil.inputStreamClone(OkHttpUtil.getIs(OkHttpUtil[url]))!!
        return buildMessageChain {
            when (ImageUtil.getImageType(ByteArrayInputStream(cloneInputStream.toByteArray()))) {
                "gif" -> {
                    val type = random.nextInt(4)
                    val out = ByteArrayOutputStream()
                    kotlin.runCatching {
                        gifDecoder.read(ByteArrayInputStream(cloneInputStream.toByteArray()))
                        gifEncoder.start(out)
                        gifEncoder.setDelay(gifDecoder.getDelay(0))
                        gifDecoder.frames.forEach {
                            gifEncoder.addFrame(getOperatedImageByType(it, type))
                        }
                        gifEncoder.finish()
                    }.onFailure {
                        Main.logger.error(it)
                        +"发生未知错误: $it"
                        +it.message!!.run {
                            "堆栈信息: ${BasicUtil.debug(this)}"
                        }
                        return@buildMessageChain
                    }
                    +box.uploadImage(ByteArrayInputStream(out.toByteArray()))
                }
                else -> {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    val bufferedImage = getOperatedImage(withContext(Dispatchers.Default) {
                        ImageIO.read(ByteArrayInputStream(cloneInputStream.toByteArray()))
                    })
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