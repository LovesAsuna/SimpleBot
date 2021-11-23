package com.hyosakura.bot.controller.misc.bangumi

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.registerDefaultPermission
import kotlinx.coroutines.flow.collect
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

object Bangumi : CompositeCommand(
    owner = Main,
    primaryName = "bangumi",
    description = "bangumi综合功能",
    parentPermission = registerDefaultPermission()
) {
    @SubCommand
    suspend fun CommandSender.galgame(keyword: String) {
        GalGame.getBangumi(this.subject!!, keyword).collect {
            sendMessage(it)
        }
        sendMessage("搜索完成！")
    }
}
