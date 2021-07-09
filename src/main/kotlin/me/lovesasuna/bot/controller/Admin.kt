package me.lovesasuna.bot.controller

import kotlinx.coroutines.delay
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.logger.ContactLogger
import me.lovesasuna.bot.util.registerPermission
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
                when (args[0].content.toLowerCase()) {
                    "confirm" -> {
                        (this.subject as? Group)?.run {
                            quit()
                            sendMessage("退群成功,感谢陪伴!")
                        } ?: sendMessage("退群失败！")
                    }
                    "setloglevel" -> {
                        sendMessage("日志等级已设置为: " + ContactLogger.setLevel(args[1].contentToString()))
                    }
                }

            }
        }
        confim = false
    }
}