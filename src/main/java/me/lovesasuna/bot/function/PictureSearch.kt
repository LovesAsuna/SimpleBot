package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.Listener
import me.lovesasuna.bot.util.NetWorkUtil
import me.lovesasuna.bot.util.PictureSearchUtil
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.lang.StringBuilder

class PictureSearch : Listener{
    private val map = HashMap<Long, Boolean>()

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val senderID = event.sender.id
        if (message == "/搜图" && !map.contains(senderID)) {
            map.put(senderID, true)
            event.reply(At(event.sender as Member) + "请发送图片")
        }

        if (map[senderID] != null && image != null) {
            event.reply(At(event.sender as Member) + "查找中!")
            val results = PictureSearchUtil.search(image.queryUrl())
            if (results.size == 0) {
                event.reply("未查找到结果!")
                return true
            }
            results.forEach {
                val builder = StringBuilder()
                it.extUrls.forEach {
                    builder.append(it).append("\n")
                }
                event.reply(event.uploadImage(NetWorkUtil.fetch(it.thumbnail)!!.first) as Message + PlainText("\n相似度: ${it.similarity} \n相关链接: \n${builder.toString().replace(Regex("\n$"), "")}"))
            }
            map.remove(senderID)
        }

        return true
    }


}