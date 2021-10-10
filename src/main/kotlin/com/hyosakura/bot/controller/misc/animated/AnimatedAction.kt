package com.hyosakura.bot.controller.misc.animated

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.image.ImageUtil
import com.hyosakura.bot.util.image.gif.AnimatedGifEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.Message
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage

/**
 * 动图顶层接口
 *
 * @author LovesAsuna
 **/
abstract class AnimatedAction {
    /**
     * 以成员头像为基础生成动图
     *
     * @param target 成员
     * @return 动图消息
     */
    abstract suspend fun action(target: Member): Message

    suspend fun getHead(target: Member, size: Int): BufferedImage? {
        kotlin.runCatching {
            return withContext(Dispatchers.IO) {
                ImageUtil.getMemberAvatar(target, size)
            }
        }.onFailure {
            Main.logger.error(it)
        }
        return null
    }

    fun getRotatedHead(head: BufferedImage, width: Int, height: Int): BufferedImage {
        return makeImage(width, height).also {
            it.createGraphics().apply {
                antialias()
                clip = Ellipse2D.Double(0.0, 0.0, width.toDouble(), height.toDouble())
                rotate(Math.toRadians(-40.0), (width / 2).toDouble(), (height / 2).toDouble())
                drawImage(head.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, width, height, null)
                dispose()
            }
        }
    }

    fun makeImage(width: Int, height: Int): BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)

    protected fun Graphics2D.antialias() {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }

    protected fun encode(
        target: BufferedImage,
        animatedFrames: List<BufferedImage>,
        encoder: AnimatedGifEncoder,
        info: Array<IntArray>,
        width: Int,
        height: Int
    ): Boolean {
        for (i in animatedFrames.indices) {
            val background = animatedFrames[i]
            kotlin.runCatching {
                makeImage(width, height).also {
                    it.createGraphics().apply {
                        color = Color.WHITE
                        fillRect(0, 0, width, height)
                        antialias()
                        drawImage(
                            target,
                            info[i][0],
                            info[i][1],
                            info[i][2],
                            info[i][3],
                            null
                        )

                        drawImage(background, 0, 0, width, height, null)
                        dispose()
                        encoder.addFrame(it)
                    }
                }
            }.onFailure {
                Main.logger.error(it)
                return false
            }
        }
        return true
    }

    interface AnimatedFrame {
        val animatedFrames: List<BufferedImage>
    }
}