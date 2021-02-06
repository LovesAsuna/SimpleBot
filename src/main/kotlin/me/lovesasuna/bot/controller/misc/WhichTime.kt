package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author LovesAsuna
 **/
object WhichTime : SimpleCommand(
    owner = Main,
    primaryName = "几点了",
    description = "发送含有当前时间的图片",
    parentPermission = registerDefaultPermission()
) {
    val formatter = DateTimeFormatter.ofPattern("HH-mm")

    @Handler
    suspend fun CommandSender.handle() {
        val time = formatter.format(LocalDateTime.now())
        sendMessage(OkHttpUtil.getIs(OkHttpUtil["https://ty.kuku.me/images/time/$time.jpg"]).uploadAsImage(getGroupOrNull()!!))
    }
}