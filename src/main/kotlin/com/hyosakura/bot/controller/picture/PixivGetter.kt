package com.hyosakura.bot.controller.picture

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.Jsoup
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PixivGetter : CompositeCommand(
    owner = Main,
    primaryName = "pixiv",
    description = "反代理P站图片",
    parentPermission = registerDefaultPermission()
), ReCallable {
    @SubCommand
    suspend fun CommandSender.work(ID: Int) {
        val root = withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Request.getJson("https://api.obfs.dev/api/pixiv/illust?id=$ID")
        }
        if (root["error"] != null) {
            var text = root["error"]["message"].asText()
            if (text.isEmpty()) {
                text = root["error"]["user_message"].asText()
            }
            sendMessage("Error: $text")
            return
        }
        val illustration = root["illust"]
        val title = illustration["title"].asText()
        val caption = illustration["caption"].asText()
        val userName = illustration["user"]["name"].asText()
        val account = illustration["user"]["account"].asText()
        var time = LocalDateTime.parse(illustration["create_date"].asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val timeText = DateTimeFormatter.ofPattern(
            "yyyy年MM月dd日${
                if (time.hour <= 12) {
                    "上午"
                } else if (time.hour <= 18) {
                    time = time.minusHours(12)
                    "下午"
                } else {
                    time = time.minusHours(12)
                    "晚上"
                }
            }HH点mm分"
        ).format(time)
        val tags = illustration["tags"].toString()
        val count = illustration["page_count"].asInt()
        val view = illustration["total_view"].asInt()
        val mark = illustration["total_bookmarks"].asInt()
        val r18 = tags.contains(Regex("R-[1-9]+"))
        sendMessage("获取中,请稍后..")
        var `is`: InputStream?
        val message = buildMessageChain {
            +"""
            [pid$ID]
            https://www.pixiv.net/artworks/$ID
            原图: 
            """.trimIndent()
            if (count == 1) {
                Main.scheduler.withTimeOut({
                    `is` =
                        Request.getIs("https://pixiv.re/$ID.jpg")
                    +`is`!!.uploadAsImage(getGroupOrNull()!!)
                }, 60000) {
                    +"\n图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者\n"
                }
            } else {
                +"该作品共有${count}张图片${if (count > 5) ",预览前5张" else ""}"
                repeat(if (count > 5) 5 else count) {
                    Main.scheduler.withTimeOut({
                        `is` = Request.getIs("https://pixiv.re/$ID-${it + 1}.jpg")
                        +`is`!!.uploadAsImage(getGroupOrNull()!!)
                    }, 60000) {
                        +"\n图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者\n"
                    }
                }
            }
            +"""
            标题: $title
            画师: $userName＠$account
            投稿时间: $timeText
            收藏数: $mark
            查看数: $view
            R18: $r18
            直连链接: https://pixiv.re/${
                if (count == 1) {
                    ID
                } else {
                    "$ID{1-$count}"
                }
            }.jpg
            """.trimIndent()
            +"\n"
            +Jsoup.parse(caption).let {
                it.select("br").append("\\n")
                it.text().replace("\\n", "\n")
            }
        }
        sendMessage(message).also {
            if (r18) {
                it?.recallIn(5000)
            }
        }
    }
}