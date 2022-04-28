package com.hyosakura.bot.util.image

import net.mamoe.mirai.contact.Member
import java.awt.Dimension
import java.awt.Image
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


object ImageUtil {
    /**
     *
     * @param bufferedImage
     * 图片
     * @param angel 旋转角度
     *
     * @return
     */
    fun rotateImage(bufferedImage: BufferedImage, angel: Int): BufferedImage {
        var revisedAngel = angel
        if (revisedAngel < 0) {
            // 将负数角度，纠正为正数角度
            revisedAngel += 360
        }
        val imageWidth = bufferedImage.getWidth(null)
        val imageHeight = bufferedImage.getHeight(null)
        // 计算重新绘制图片的尺寸
        val rectangle = calculatorRotatedSize(Rectangle(Dimension(imageWidth, imageHeight)), revisedAngel)
        // 获取原始图片的透明度
        val type = bufferedImage.colorModel.transparency
        val newImage: BufferedImage?
        newImage = BufferedImage(rectangle.width, rectangle.height, type)
        val graphics = newImage.createGraphics()
        // 平移位置
        graphics.translate((rectangle.width - imageWidth) / 2, (rectangle.height - imageHeight) / 2)
        // 旋转角度
        graphics.rotate(Math.toRadians(revisedAngel.toDouble()), imageWidth / 2.toDouble(), imageHeight / 2.toDouble())
        // 绘图
        graphics.drawImage(bufferedImage, null, null)
        return newImage
    }

    /**
     * 计算旋转后的尺寸
     *
     * @param src
     * @param angel 旋转角度
     * @return
     */
    private fun calculatorRotatedSize(src: Rectangle, angel: Int): Rectangle {
        var revisedAngel = angel
        if (revisedAngel >= 90) {
            if (revisedAngel / 90 % 2 == 1) {
                val temp = src.height
                src.height = src.width
                src.width = temp
            }
            revisedAngel %= 90
        }
        val r = sqrt((src.height * src.height + src.width * src.width).toDouble()) / 2
        val len = 2 * sin(Math.toRadians(revisedAngel.toDouble()) / 2) * r
        val angelAlpha = (Math.PI - Math.toRadians(revisedAngel.toDouble())) / 2
        val angelDaltaWidth = atan(src.height.toDouble() / src.width)
        val angelDaltaHeight = atan(src.width.toDouble() / src.height)
        val lenDaltaWidth = (len * cos(Math.PI - angelAlpha - angelDaltaWidth)).toInt()
        val lenDaltaHeight = (len * cos(Math.PI - angelAlpha - angelDaltaHeight)).toInt()
        val desWidth = src.width + lenDaltaWidth * 2
        val desHeight = src.height + lenDaltaHeight * 2
        return Rectangle(Dimension(desWidth, desHeight))
    }

    fun mirrorImage(bufferedImage: BufferedImage): BufferedImage {
        val width = bufferedImage.width
        val height = bufferedImage.height
        for (j in 0 until height) {
            var l = 0
            var r = width - 1
            while (l < r) {
                val pl = bufferedImage.getRGB(l, j)
                val pr = bufferedImage.getRGB(r, j)
                bufferedImage.setRGB(l, j, pr)
                bufferedImage.setRGB(r, j, pl)
                l++
                r--
            }
        }
        return bufferedImage
    }

    /**
     * @param bufferedImage 图像
     * @param mode 1为水平反转,2为竖直反转
     */
    fun reverseImage(bufferedImage: BufferedImage, mode: Int): BufferedImage {
        val width = bufferedImage.width
        val height = bufferedImage.height
        var before: Int
        var after: Int
        when (mode) {
            1 -> {
                val midWidth = width / 2
                for (i in 0 until height) {
                    for (j in 0 until midWidth) {
                        before = bufferedImage.getRGB(j, i)
                        after = bufferedImage.getRGB(j + midWidth, i)
                        bufferedImage.setRGB(j, i, after)
                        bufferedImage.setRGB(j + midWidth, i, before)
                    }
                }
            }
            2 -> {
                val midHeight = height / 2
                for (i in 0 until width) {
                    for (j in 0 until midHeight) {
                        before = bufferedImage.getRGB(i, j)
                        after = bufferedImage.getRGB(i, j + midHeight)
                        bufferedImage.setRGB(i, j, after)
                        bufferedImage.setRGB(i, j + midHeight, before)
                    }
                }
            }
        }
        return bufferedImage
    }

    /**
     * 当多次使用时务必传递输入流的副本
     */
    fun getImageType(`in`: InputStream): String? {
        val readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(`in`))
        return if (readers.hasNext()) {
            readers.next().formatName
        } else null
    }

    /**
     * 将 [BufferedImage] 转换为字节数组
     *
     * @param image 图像
     * @return 字节数组
     */
    fun imageToByte(image: BufferedImage): ByteArray {
        val out = ByteArrayOutputStream()
        ImageIO.write(image, "png", ImageIO.createImageOutputStream(out))
        return out.toByteArray()
    }

    /**
     * 将 [BufferedImage] 转换为输入流
     *
     * @param image 图像
     * @return 输入流
     */
    fun imageToStream(image: BufferedImage): InputStream = ByteArrayInputStream(imageToByte(image))

    /**
     * 获取成员的头像
     *
     * @param target 目标成员
     * @return 头像对应的 [BufferedImage]
     */
    fun getMemberAvatar(target: Member, size: Int): BufferedImage {
        val head = ImageIO.read(URL(target.avatarUrl))
        val formattedHead = BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR).also {
            it.createGraphics().apply {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                val shape = Ellipse2D.Double(
                    0.0,
                    0.0,
                    size.toDouble(),
                    size.toDouble()
                )
                clip = shape
                drawImage(
                    head.getScaledInstance(size, size, Image.SCALE_SMOOTH),
                    0,
                    0,
                    size,
                    size,
                    null
                )
                dispose()
            }
        }
        return formattedHead
    }
}