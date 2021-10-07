package com.hyosakura.bot.controller.picture.search

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.picture.PixivGetter.work
import com.hyosakura.bot.util.BasicUtil
import com.hyosakura.bot.util.network.OkHttpUtil
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

object PictureSearch : SimpleCommand(
    owner = Main,
    primaryName = "搜图",
    description = "以图搜图",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(type: Int, image: Image) {
        val source = when (type) {
            1 -> {
                sendMessage(At(user!!) + "Saucenao查找中!")
                Saucenao
            }
            2 -> {
                sendMessage(At(user!!) + "Ascii2d查找中!")
                Ascii2d
            }
            else -> Saucenao
        }
        val imgUrl = image.queryUrl()
        Main.logger.debug("图片URL: $imgUrl")
        val results = source.search(imgUrl)
        if (results.isEmpty()) {
            sendMessage("未查找到结果!")
            return
        }
        sendMessage("搜索完成!")
        Main.logger.debug(results.toString())
        results.forEach { result ->
            val builder = StringBuilder()
            result.extUrls.forEach {
                if (it.contains("pixiv") && it.contains(Regex("(illust)|(artwork)"))) {
                    if (result.similarity > 90) {
                        work(BasicUtil.extractInt(it))
                    }
                }
                builder.append(it).append("\n")
            }
            Main.scheduler.withTimeOut(suspend {
                val uploadImage =
                    OkHttpUtil.getIs(OkHttpUtil[result.thumbnail]).uploadAsImage(getGroupOrNull()!!) as Message
                sendMessage(
                    uploadImage + PlainText(
                        "\n相似度: ${result.similarity} \n画师名: ${result.memberName} \n相关链接: \n${
                            builder.toString().replace(Regex("\n$"), "")
                        }"
                    )
                )
                uploadImage
            }, 7500) {
                sendMessage("缩略图上传超时")
                sendMessage(
                    PlainText(
                        "空图像(上传失败)\n相似度: ${result.similarity} \n画师名: ${result.memberName} \n相关链接: \n${
                            builder.toString().replace(Regex("\n$"), "")
                        }"
                    )
                )
            }
        }
    }
}