package com.hyosakura.bot.util.image

import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.imageio.ImageIO

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
        val r = Math.sqrt((src.height * src.height + src.width * src.width).toDouble()) / 2
        val len = 2 * Math.sin(Math.toRadians(revisedAngel.toDouble()) / 2) * r
        val angelAlpha = (Math.PI - Math.toRadians(revisedAngel.toDouble())) / 2
        val angelDaltaWidth = Math.atan(src.height.toDouble() / src.width)
        val angelDaltaHeight = Math.atan(src.width.toDouble() / src.height)
        val lenDaltaWidth = (len * Math.cos(Math.PI - angelAlpha - angelDaltaWidth)).toInt()
        val lenDaltaHeight = (len * Math.cos(Math.PI - angelAlpha - angelDaltaHeight)).toInt()
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

    fun getImageType(file: File) = getImageType(FileInputStream(file))

    fun imageToByte(image : BufferedImage): ByteArray {
        val out = ByteArrayOutputStream()
        ImageIO.write(image, "png", ImageIO.createImageOutputStream(out))
        return out.toByteArray()
    }
}