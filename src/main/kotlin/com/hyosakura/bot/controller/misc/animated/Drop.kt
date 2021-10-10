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
class Drop(val contact: Contact) : AnimatedAction() {
    override suspend fun action(target: Member): Message {
        val hdW = 250
        val head: BufferedImage = getHead(target, hdW) ?: return PlainText("图片生成发生错误!")
        val background = animatedFrames[0]

        // 处理头像
        val formattedHead = getRotatedHead(head, hdW, hdW)

        // 重合图片
        val bufferedImage = makeImage(background.width, background.height).also {
            it.createGraphics().apply {
                antialias()
                drawImage(formattedHead, 134, 220, hdW, 210, null)
                drawImage(background, 0, 0, null)
                dispose()
            }
        }
        return ImageUtil.imageToStream(bufferedImage).uploadAsImage(contact)
    }

    companion object : AnimatedFrame {
        override val animatedFrames: List<BufferedImage> by lazy {
            listOf(
                ImageIO.read(
                    URL("https://cdn.jsdelivr.net/gh/coide-SaltedFish/SereinFish@6aecc3a21df77a6b1b0b0b26cdb7c3495d90fb0c/src/main/resources/image/diu.png")
                )
            )
        }
    }
}