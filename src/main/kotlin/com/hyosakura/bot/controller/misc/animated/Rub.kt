package com.hyosakura.bot.controller.misc.animated

import com.hyosakura.bot.util.image.ImageUtil
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
 * 蹭脸
 *
 * @author LovesAsuna
 **/
class Rub(val contact: Contact) : AnimatedAction() {
    override suspend fun action(target: Member): Message {
        val baos = ByteArrayOutputStream()
        val bgWH = 240
        val head = ImageUtil.getMemberAvatar(target, 80) //得到头像
        val encoder = AnimatedGifEncoder().also {
            it.setSize(bgWH, bgWH)
            it.start(baos)
            it.setDelay(50)
            it.setRepeat(13)
        }

        // 头像位置信息(x,y,w,h)
        val imageHeadInfo = arrayOf(
            intArrayOf(37, 85, 72, 85),
            intArrayOf(46, 98, 74, 83),
            intArrayOf(65, 97, 74, 82),
            intArrayOf(51, 81, 74, 87),
            intArrayOf(57, 109, 71, 82),
            intArrayOf(60, 100, 58, 81)
        )
        if (!encode(head, animatedFrames, encoder, imageHeadInfo, bgWH, bgWH)) {
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
            for (i in 1..6) {
                list.add(ImageIO.read(URL("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21d/src/main/resources/image/ceng/$i.png")))
            }
            list
        }
    }
}