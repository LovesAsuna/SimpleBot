package me.lovesasuna.bot.function

import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.File

class Sort : Listener {
    companion object {
        fun sort(suffix: String): String {
            var notExist = 1
            var still = false
            var add = 0
            for (i in 1..DownloadImage.max) {
                val fileName = DownloadImage.path + File.separator + i + "."
                val file = File(fileName + suffix)
                /*如果文件不存在*/
                if (!file.exists()) {
                    if (!still) {
                        notExist = i
                    }
                    still = true
                } else {
                    if (still) {
                        file.renameTo(File(DownloadImage.path + File.separator + (notExist + add) + "." + suffix))
                        add++
                    }
                }
            }
            return "成功整理 " + add + " 张" + suffix + "图片"
        }
    }

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val sender = event.sender.id
        event as FriendMessageEvent
        if (sender == Config.data.admin) {
            when {
                message == "/photoinit" -> {
                    DownloadImage.init()
                    event.reply("捕捉器重启成功! 索引: " + DownloadImage.max)
                    return true
                }
                message == "/getcount" -> {
                    event.reply(DownloadImage.max.toString())
                    return true
                }
                message.startsWith("/sort") -> {
                    event.reply(sort(message.split(" ").toTypedArray()[1]))
                    return true
                }
                else -> {
                    return false
                }
            }
        } else {
            return false
        }
    }
}