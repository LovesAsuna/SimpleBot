package me.lovesasuna.bot.function

import kotlinx.coroutines.delay
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.IOException

class Admin : FunctionListener {
    private var confim = false

    @Throws(IOException::class)
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        event as GroupMessageEvent
        val senderID = event.sender.id
        if (senderID == Config.data.admin) {
            if (message == "/quit" && !confim) {
                event.reply("请在十秒内输入/quit confirm进行确认！")
                confim = true
                delay(10 * 1000)
            } else if (message == "/quit confirm" && confim) {
                event.reply("退群成功,感谢陪伴!")
                event.group.quit()
            }
            confim = false
        }
        return true
    }
}