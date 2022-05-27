package com.hyosakura.bot.controller.picture.search

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.picture.PixivGetter.work
import com.hyosakura.bot.util.BasicUtil
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

object PictureSearch : SimpleCommand(
    owner = Main,
    primaryName = "搜图",
    description = "以图搜图",
    parentPermission = registerDefaultPermission()
) {
    private val sourceMap = mapOf(
        1 to SauceNAO,
        2 to Ascii2d
    )

    @Handler
    suspend fun CommandSender.handle(type: Int, image: Image) {
        val source = sourceMap[type] ?: sourceMap[0]!!
        sendMessage(At(user!!) + "${source.name}查找中!")
        val results = getResult(source, image) ?: return
        fun MessageChainBuilder.add(result: PictureResult) {
            +"相似度: ${result.similarity}\n"
            +"画师名: ${result.memberName}\n"
            +"相关链接:\n"
            result.extUrls!!.forEach {
                +"$it\n"
            }
        }
        results.forEach { result ->
            BasicUtil.withTimeOut({
                val uploadImage = Request.getIs(result.thumbnail!!).uploadAsImage(getGroupOrNull()!!)
                sendMessage(
                    buildMessageChain {
                        +uploadImage
                        add(result)
                    }
                )
                uploadImage
            }, 7500) {
                sendMessage("缩略图上传超时")
                sendMessage(
                    buildMessageChain {
                        +"空图像(上传失败)\n"
                        add(result)
                    }
                )
            }
            result.extUrls!!.forEach {
                if (it.contains("pixiv") && it.contains(Regex("(illust)|(artwork)"))) {
                    if (result.similarity!! > 90) {
                        work(BasicUtil.extractInt(it))
                    }
                }
            }
        }
    }
}