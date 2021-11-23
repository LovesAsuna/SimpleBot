package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLEncoder

object Baike : SimpleCommand(
    owner = Main,
    primaryName = "baike",
    description = "百度百科",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle(context: String) {
        val url = "https://baike.baidu.com/item/${URLEncoder.encode(context, "UTF-8")}"
        val root = withContext(Dispatchers.IO) {
            Jsoup.parse(URL(url), 5000)
        }
        sendMessage(root.select("meta[name=description]").attr("content").run {
            this.ifEmpty {
                "百度百科未收录此词条!"
            }
        })
    }
}