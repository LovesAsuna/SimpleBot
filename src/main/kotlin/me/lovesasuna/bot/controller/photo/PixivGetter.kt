package me.lovesasuna.bot.controller.photo

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

object PixivGetter : SimpleCommand(
    owner = Main,
    primaryName = "pixiv",
    description = "反代理P站图片",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(command: String, ID: Int) {
        if (command != "work") {
            return
        }
        sendMessage("获取中,请稍后..")
        val reader = OkHttpUtil.getIs(
            OkHttpUtil.post(
                "https://api.pixiv.cat/v1/generate", mapOf(
                    "p" to "$ID"
                )
            )
        ).bufferedReader()
        val root = BotData.objectMapper.readTree(reader.readLine())
        val list = root.get("original_url") ?: root.get("original_urls")
        if (list == null) {
            sendMessage("该作品不存在或已被删除!")
            return
        }
        val size = list.size()
        var originInputStream: InputStream?
        if (size == 0) {
            if (BotData.debug) sendMessage("尝试复制IO流")
            Main.scheduler.withTimeOut(suspend {
                originInputStream =
                    OkHttpUtil.getIs(OkHttpUtil["https://api.kuku.me/pixiv/picbyurl?url=${list.asText()}"])
                sendMessage(originInputStream!!.uploadAsImage(getGroupOrNull()!!))
                sendMessage("获取完成!")
            }, 60 * 1000) {
                sendMessage("图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者")
            }
        } else {
            sendMessage("该作品共有${size}张图片")
            repeat(size) {
                originInputStream =
                    OkHttpUtil.getIs(OkHttpUtil["https://api.kuku.me/pixiv/picbyurl?url=${list[it].asText()}"])
                sendMessage(originInputStream!!.uploadAsImage(getGroupOrNull()!!))
            }
            sendMessage("获取完成!")
        }
    }
}