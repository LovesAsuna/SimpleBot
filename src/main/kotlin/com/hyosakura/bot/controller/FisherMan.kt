package com.hyosakura.bot.controller

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

object FisherMan : SimpleCommand(
    owner = Main,
    primaryName = "摸鱼日历",
    description = "摸鱼日历",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle() {
        sendMessage(
            withContext(Dispatchers.IO) {
                Request.getIs("https://api.kukuqaq.com/tool/fishermanCalendar?preview")
            }
            .uploadAsImage(this.subject!!)
        )
    }
}