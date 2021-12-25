package com.hyosakura.bot.controller.misc.animated

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.image.ImageUtil
import com.hyosakura.bot.util.image.gif.AnimatedGifEncoder
import com.hyosakura.bot.util.image.gif.GifDecoder
import com.hyosakura.bot.util.network.Request
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author LovesAsuna
 **/
class Chew(val contact: Contact) : AnimatedAction() {
    override suspend fun action(target: Member): Message {
        val diameter = 38
        val head = ImageUtil.getMemberAvatar(target, diameter).also {
            it.createGraphics().apply {
                antialias()
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f)
                fillOval(21, 0, 8, 10)
                dispose()
            }
        }
        val baos = ByteArrayOutputStream()
        val decoder = GifDecoder()
        decoder.read(ByteArrayInputStream(gifBytes)).also {
            if (it != GifDecoder.STATUS_OK) {
                return PlainText("在生成图片时出现了一点点错误:$it")
            }
        }
        val encoder = AnimatedGifEncoder().also {
            it.start(baos)
            it.setRepeat(decoder.loopCount)
            it.setDelay(decoder.getDelay(0))
        }

        for (i in 0 until decoder.frameCount) {
            kotlin.runCatching {
                decoder.getFrame(i).also {
                    it.createGraphics().apply {
                        antialias()
                        drawImage(head, 0, it.height - head.height + 6, diameter, diameter, null)
                        drawImage(animatedFrames[0], 0, 0, it.width, it.height, null)
                        dispose()
                        encoder.addFrame(it)
                    }
                }
            }.onFailure {
                Main.logger.error(it)
                return PlainText("在生成图片时出现了一点点错误")
            }
        }
        return if (encoder.finish()) {
            ByteArrayInputStream(baos.toByteArray()).uploadAsImage(contact)
        } else {
            PlainText("在生成图片时出现了一点点错误")
        }
    }

    companion object : AnimatedFrame {
        val gifBytes: ByteArray by lazy {
            runBlocking {
                Request.get("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21d/src/main/resources/image/jiao/jiao.gif")
                    .readBytes()
            }
        }

        override val animatedFrames: List<BufferedImage> by lazy {
            listOf(
                ImageIO.read(
                    URL("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21d/src/main/resources/image/jiao/jiao_top")
                )
            )
        }
    }
}