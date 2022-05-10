package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

object FisherMan : SimpleCommand(
    owner = Main,
    primaryName = "摸鱼人日历",
    description = "摸鱼人日历",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val root = Request.getJson("https://api.kukuqaq.com/tool/fishermanCalendar?preview")
        sendMessage(
            Request.getIs(root["data"]["url"].asText())
                .uploadAsImage(this.subject!!)
        )
    }
}