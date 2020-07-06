package me.lovesasuna.bot.util.photo

import java.awt.Dimension
import java.awt.Image
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
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
            revisedAngel = revisedAngel + 360
        }
        val imageWidth = bufferedImage.getWidth(null)
        val imageHeight = bufferedImage.getHeight(null)
        // 计算重新绘制图片的尺寸
        val rectangle = calculatorRotatedSize(Rectangle(Dimension(imageWidth, imageHeight)), revisedAngel)
        // 获取原始图片的透明度
        val type = bufferedImage.colorModel.transparency
        var newImage: BufferedImage?
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
            revisedAngel = revisedAngel % 90
        }
        val r = Math.sqrt((src.height * src.height + src.width * src.width).toDouble()) / 2
        val len = 2 * Math.sin(Math.toRadians(revisedAngel.toDouble()) / 2) * r
        val angel_alpha = (Math.PI - Math.toRadians(revisedAngel.toDouble())) / 2
        val angel_dalta_width = Math.atan(src.height.toDouble() / src.width)
        val angel_dalta_height = Math.atan(src.width.toDouble() / src.height)
        val len_dalta_width = (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width)).toInt()
        val len_dalta_height = (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height)).toInt()
        val des_width = src.width + len_dalta_width * 2
        val des_height = src.height + len_dalta_height * 2
        return Rectangle(Dimension(des_width, des_height))
    }

    fun reverseImage(bufferedImage: BufferedImage) : BufferedImage {
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
}