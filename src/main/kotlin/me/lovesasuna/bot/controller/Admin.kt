package me.lovesasuna.bot.controller

import kotlinx.coroutines.delay
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.file.Config
import net.mamoe.mirai.event.events.GroupMessageEvent
import java.io.IOException

class Admin : FunctionListener {
    private var confim = false

    @Throws(IOException::class)
    override suspend fun execute(box: MessageBox): Boolean {
        box.event as GroupMessageEvent
        val senderID = box.event.sender.id
        if (Config.data.Admin.contains(senderID)) {
            if (box.text() == "/quit" && !confim) {
                box.reply("请在十秒内输入/quit confirm进行确认！")
                confim = true
                delay(10 * 1000)
            } else if (box.text() == "/quit confirm" && confim) {
                box.reply("退群成功,感谢陪伴!")
                box.event.group.quit()
            }
            confim = false
        }
        return true
    }
}