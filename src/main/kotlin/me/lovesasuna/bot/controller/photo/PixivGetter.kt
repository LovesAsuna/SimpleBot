package me.lovesasuna.bot.controller.photo

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.OriginMain
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

object PixivGetter : SimpleCommand(
    owner = Main,
    primaryName = "pixiv work"
) {
    @Handler
    suspend fun CommandSender.handle(ID: Int) {
        sendMessage("获取中,请稍后..")
        val reader = NetWorkUtil.post(
            "https://api.pixiv.cat/v1/generate", "p=$ID".toByteArray(),
            arrayOf("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
        )!!.second.bufferedReader()
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
            OriginMain.scheduler.withTimeOut(suspend {
                originInputStream =
                    NetWorkUtil["https://api.kuku.me/pixiv/picbyurl?url=${list.asText()}"]!!.second
                sendMessage(originInputStream!!.uploadAsImage(getGroupOrNull()!!))
                sendMessage("获取完成!")
            }, 60 * 1000) {
                sendMessage("图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者")
            }
        } else {
            sendMessage("该作品共有${size}张图片")
            repeat(size) {
                originInputStream =
                    NetWorkUtil["https://api.kuku.me/pixiv/picbyurl?url=${list[it].asText()}"]!!.second
                sendMessage(originInputStream!!.uploadAsImage(getGroupOrNull()!!))
            }
            sendMessage("获取完成!")
        }
    }
}