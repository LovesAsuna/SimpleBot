package com.hyosakura.bot.controller.picture.search

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.OkHttpUtil
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

object AnimeSearch : SimpleCommand(
    owner = Main,
    primaryName = "查番",
    description = "以图搜番",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(image: Image) {
        val source = TraceMoe
        val imgUrl = image.queryUrl()
        Main.logger.debug("图片URL: $imgUrl")
        val results = source.search(imgUrl)
        if (results.isEmpty()) {
            sendMessage("未查找到结果!")
            return
        }
        sendMessage("搜索完成!")
        Main.logger.debug(results.toString())
        fun MessageChainBuilder.add(result: AnimeResult) {
            +"${result.episodes}集 每集${result.duration}分钟\n"
            +"${result.startDate} to ${result.endDate}\n"
            +"相关链接:\n"
            for (url in result.extUrls!!) {
                +"${url[0]}\n"
                +"${url[1]}\n"
            }
            +"Banner:\n"
        }
        results.forEach { result ->
            Main.scheduler.withTimeOut(suspend {
                sendMessage(
                    buildMessageChain {
                        +"相似度: ${result.similarity}\n"
                        + "目标画面所处时长: ${result.from}-${result.to}\n"
                        +"番名: ${result.title}\n"
                        +"Cover:\n"
                        +withContext(Dispatchers.IO) {
                            OkHttpUtil.getIs(OkHttpUtil[result.cover!!]).uploadAsImage(getGroupOrNull()!!)
                        }
                        add(result)
                        +withContext(Dispatchers.IO) {
                            OkHttpUtil.getIs(OkHttpUtil[result.banner!!]).uploadAsImage(getGroupOrNull()!!)
                        }
                    }
                )
            }, 15000) {
                sendMessage("缩略图上传超时")
                sendMessage(
                    buildMessageChain {
                        +"相似度: ${result.similarity}\n"
                        +"番名: ${result.title}\n"
                        +"Cover:\n"
                        +"上传失败\n"
                        add(result)
                        +"上传失败\n"
                    }
                )
            }
        }
    }
}