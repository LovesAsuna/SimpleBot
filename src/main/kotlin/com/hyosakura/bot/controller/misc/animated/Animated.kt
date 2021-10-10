package com.hyosakura.bot.controller.misc.animated

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Member

/**
 * @author LovesAsuna
 **/
object Animated : CompositeCommand(
    owner = Main,
    primaryName = "动图",
    description = "生成gif图片",
    parentPermission = registerDefaultPermission()
) {
    @SubCommand(value = ["drop", "diu", "丢"])
    suspend fun CommandSender.drop(target: Member) {
        sendMessage(Drop(this.subject!!).action(target))
    }

    @SubCommand
    suspend fun CommandSender.mua(target: Member) {
        sendMessage(Mua(this.subject!!).action(target))
    }

    @SubCommand(value = ["爬", "pa"])
    suspend fun CommandSender.climb(target: Member) {
        sendMessage(Climb(this.subject!!).action(target))
    }

    @SubCommand(value = ["jiao", "嚼", "qia", "恰"])
    suspend fun CommandSender.chew(target: Member) {
        sendMessage(Chew(this.subject!!).action(target))
    }

    @SubCommand(value = ["rua", "摸"])
    suspend fun CommandSender.rua(target: Member) {
        sendMessage(Rua(this.subject!!).action(target))
    }

    @SubCommand(value = ["ceng", "rub", "蹭"])
    suspend fun CommandSender.rub(target: Member) {
        sendMessage(Rub(this.subject!!).action(target))
    }
}