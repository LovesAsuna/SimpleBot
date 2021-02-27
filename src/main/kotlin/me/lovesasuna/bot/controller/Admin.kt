package me.lovesasuna.bot.controller

import kotlinx.coroutines.delay
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.registerPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

object Admin : RawCommand(
    owner = Main,
    primaryName = "quit",
    description = "机器人退群(敏感操作)",
    parentPermission = registerPermission("admin", "管理员权限")
) {
    private var confim = false


    override suspend fun CommandSender.onCommand(args: MessageChain) {
        when (args.size) {
            0 -> {
                sendMessage("请在十秒内输入/quit confirm进行确认！")
                confim = true
                delay(10 * 1000)
            }
            1 -> {
                if (args[0].content == "confirm") {
                    (this.subject as? Group)?.run {
                        quit()
                        sendMessage("退群成功,感谢陪伴!")
                    } ?: sendMessage("退群失败！")
                }
            }
        }
        confim = false
    }
}