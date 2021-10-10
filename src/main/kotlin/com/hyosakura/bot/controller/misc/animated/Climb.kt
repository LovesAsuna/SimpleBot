package com.hyosakura.bot.controller.misc.animated

import com.hyosakura.bot.util.image.ImageUtil
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author LovesAsuna
 **/
class Climb(val contact: Contact) : AnimatedAction() {
    override suspend fun action(target: Member): Message {
        val hdW = 65
        val head: BufferedImage = getHead(target, hdW) ?: return PlainText("图片生成发生错误!")
        val background = animatedFrames.random()

        // 处理头像
        val formattedHead = getRotatedHead(head, hdW, hdW)

        // 重合图片
        val bufferedImage = makeImage(background.width, background.width).also {
            it.createGraphics().apply {
                antialias()
                drawImage(background, 0, 0, null)
                drawImage(formattedHead, 0, it.height - hdW, hdW, hdW, null)
                dispose()
            }
        }
        return ImageUtil.imageToStream(bufferedImage).uploadAsImage(contact)
    }

    companion object : AnimatedFrame {
        override val animatedFrames: List<BufferedImage> by lazy {
            val list = mutableListOf<BufferedImage>()
            for (i in 1..16) {
                list.add(ImageIO.read(URL("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21d/src/main/resources/image/pa/$i.jpg")))
            }
            list
        }

    }
}