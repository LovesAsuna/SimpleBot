package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.logger.debug
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.InputStream

object PixivGetter : CompositeCommand(
    owner = Main,
    primaryName = "pixiv",
    description = "反代理P站图片",
    parentPermission = registerDefaultPermission()
) {
    @SubCommand
    suspend fun CommandSender.work(ID: Int) {
        val count: Int
        val root = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            OkHttpUtil.getJson("https://api.obfs.dev/api/pixiv/illust?id=$ID")
        }
        if (root["error"] != null) {
            sendMessage("该作品不存在或已被删除!")
            return
        }
        count = root["illust"]["page_count"].asInt()
        val tags = root["illust"]["tags"].toString()
        if (tags.contains(Regex("R-[1-9]+"))) {
            sendMessage("图片含有R18内容,禁止显示！")
            return
        }
        sendMessage("获取中,请稍后..")
        var originInputStream: InputStream?
        if (count == 1) {
            debug("尝试复制IO流")
            Main.scheduler.withTimeOut(suspend {
                originInputStream =
                    OkHttpUtil.getIs(OkHttpUtil["https://pixiv.cat/$ID.jpg"])
                sendMessage(originInputStream!!.uploadAsImage(getGroupOrNull()!!))
                sendMessage("获取完成!")
            }, 60000) {
                sendMessage("图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者")
            }
        } else {
            sendMessage("该作品共有${count}张图片")
            repeat(count) {
                Main.scheduler.withTimeOut(suspend {
                    originInputStream =
                        OkHttpUtil.getIs(OkHttpUtil["https://pixiv.cat/$ID-${it + 1}.jpg"])
                    sendMessage(originInputStream!!.uploadAsImage(getGroupOrNull()!!))
                }, 60000) {
                    sendMessage("图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者")
                }
            }
            sendMessage("获取完成!")
        }
    }
}