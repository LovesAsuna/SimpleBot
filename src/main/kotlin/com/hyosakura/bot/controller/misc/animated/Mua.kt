package com.hyosakura.bot.controller.misc.animated

import com.hyosakura.bot.util.image.gif.AnimatedGifEncoder
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author LovesAsuna
 **/
class Mua(val contact: Contact) : AnimatedAction() {
    override suspend fun action(target: Member): Message {
        val baos = ByteArrayOutputStream()
        val bgWH = 240
        val delay = 50
        val head = getHead(target, 80)
        val encoder = AnimatedGifEncoder().also {
            it.setSize(bgWH, bgWH)
            it.start(baos)
            it.setDelay(delay)
            it.setRepeat(13)
        }

        // 头像位置信息(x,y,w,h)
        val imageHeadInfo = arrayOf(
            intArrayOf(46, 117, 62, 64),
            intArrayOf(68, 107, 63, 66),
            intArrayOf(76, 107, 58, 69),
            intArrayOf(55, 123, 58, 63),
            intArrayOf(66, 123, 56, 68),
            intArrayOf(71, 122, 54, 66),
            intArrayOf(24, 146, 57, 56),
            intArrayOf(32, 128, 71, 72),
            intArrayOf(73, 110, 55, 72),
            intArrayOf(57, 118, 54, 65),
            intArrayOf(76, 114, 60, 69),
            intArrayOf(47, 137, 56, 66),
            intArrayOf(22, 149, 68, 65)
        )
        if (!encode(head!!, animatedFrames, encoder, imageHeadInfo, bgWH, bgWH)) {
            return PlainText("在生成图片时出现了一点点错误")
        }
        return if (encoder.finish()) {
            ByteArrayInputStream(baos.toByteArray()).uploadAsImage(contact)
        } else {
            PlainText("在生成图片时出现了一点点错误")
        }
    }

    companion object : AnimatedFrame {
        override val animatedFrames: List<BufferedImage> by lazy {
            val list = mutableListOf<BufferedImage>()
            for (i in 1..13) {
                list.add(ImageIO.read(URL("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21d/src/main/resources/image/mua/$i.png")))
            }
            list
        }
    }
}