package com.hyosakura.bot.controller

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.registerPermission
import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

object Admin : RawCommand(
    owner = Main,
    primaryName = "admin",
    description = "机器人退群(敏感操作)",
    parentPermission = registerPermission("admin", "管理员权限")
) {
    private var confim = false

    override suspend fun CommandSender.onCommand(args: MessageChain) {
        when (args.size) {
            1 -> {
                sendMessage("请在十秒内输入/admin quit confirm进行确认！")
                confim = true
                delay(10000)
            }
            2 -> {
                when (args[0].content.lowercase()) {
                    "confirm" -> {
                        (this.subject as? Group)?.run {
                            quit()
                            sendMessage("退群成功,感谢陪伴!")
                        } ?: sendMessage("退群失败！")
                    }
                }

            }
        }
        confim = false
    }
}