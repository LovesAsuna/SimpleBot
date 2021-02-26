package me.lovesasuna.bot.controller.qqfun

import com.sun.imageio.plugins.gif.*
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.lanzou.util.OkHttpUtil
import me.lovesasuna.bot.util.photo.ImageUtil
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.stream.MemoryCacheImageInputStream
import javax.imageio.stream.MemoryCacheImageOutputStream

/**
 * @author LovesAsuna
 */
class RepeatDetect : FunctionListener {
    private val maps: MutableMap<Long, MutableList<MessageChain>> = HashMap()
    private val gifReader = GIFImageReaderSpi().createReaderInstance() as GIFImageReader
    private val gifWriter = GIFImageWriterSpi().createWriterInstance() as GIFImageWriter
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
                when (messageChain.size) {
                    2 -> {
                        when (messageChain[1]) {
                            is PlainText -> {
                                ArrayList<Char>().apply {
                                    box.event.message.contentToString().forEach {
                                        this.add(it)
                                    }
                                    this.shuffle()
                                    val builder = StringBuilder()
                                    this.forEach { builder.append(it) }
                                    box.reply(builder.toString())
                                }
                            }
                            is Image -> {
                                val url = box.message(Image::class.java)!!.queryUrl()
                                val cloneInputStream = OkHttpUtil.inputStreamClone(OkHttpUtil.getIs(OkHttpUtil[url]))!!
                                when (ImageUtil.getImageType(ByteArrayInputStream(cloneInputStream.toByteArray()))) {
                                    // 暂时移除对gif的支持
                                    "gif/unsupported" -> {
                                        val out = ByteArrayOutputStream()
                                        gifWriter.output = MemoryCacheImageOutputStream(out)
                                        gifReader.input =
                                            MemoryCacheImageInputStream(ByteArrayInputStream(cloneInputStream.toByteArray()))
                                        gifWriter.prepareWriteSequence(null)
                                        val num = gifReader.getNumImages(true)
                                        val type = random.nextInt(4)
                                        for (i in 0 until num) {
                                            try {
                                                val image = getOperatedImageByType(gifReader.read(i), type)
                                                val metadata = gifReader.getImageMetadata(0) as GIFImageMetadata
                                                gifWriter.writeToSequence(IIOImage(image, null, metadata), null)
                                            } catch (e: Exception) {
                                                box.reply("处理第$i($num)帧时出错, 跳过处理该帧: \n${e.javaClass.typeName}")
                                            }
                                        }
                                        gifWriter.endWriteSequence()
                                        (gifWriter.output as MemoryCacheImageOutputStream).flush()
                                        box.reply(box.uploadImage(ByteArrayInputStream(out.toByteArray())))
                                    }
                                    else -> {
                                        val bufferedImage =
                                            getOperatedImage(ImageIO.read(ByteArrayInputStream(cloneInputStream.toByteArray())))
                                        box.reply(box.uploadImage(bufferedImage))
                                    }
                                }
                            }
                            else -> box.reply(messageList[2])
                        }
                    }
                    else -> box.reply(messageList[2])
                }

                messageList.clear()
                this
            }
        }
        return true
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
        if (first.contentEquals(second) && second.contentEquals(third)) {
            return true
        }
        return false
    }
}