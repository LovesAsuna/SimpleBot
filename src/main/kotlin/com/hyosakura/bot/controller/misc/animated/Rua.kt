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
 * @author LovesAsuna
 **/
class Rua(val contact: Contact) : AnimatedAction() {
    override suspend fun action(target: Member): Message {
        val baos = ByteArrayOutputStream()
        val head = ImageUtil.getMemberAvatar(target, 80)
        val encoder = AnimatedGifEncoder().also {
            it.start(baos)
            it.setDelay(80)
            it.setRepeat(0)
        }
        // 手位置信息(x,y,w,h)
        val imageHandInfo = arrayOf(
            intArrayOf(20, 22, 80, 80),
            intArrayOf(17, 29, 87, 73),
            intArrayOf(13, 37, 95, 65),
            intArrayOf(16, 30, 88, 72),
            intArrayOf(20, 22, 80, 80)
        )
        if (!encode(head, animatedFrames, encoder, imageHandInfo, animatedFrames[0].width, animatedFrames[0].width)) {
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
            for (i in 1..5) {
                list.add(ImageIO.read(URL("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21d/src/main/resources/image/mo/$i.png")))
            }
            list
        }
    }
}