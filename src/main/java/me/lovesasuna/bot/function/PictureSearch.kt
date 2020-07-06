package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.network.NetWorkUtil
import me.lovesasuna.bot.util.PictureSearchUtil
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.lang.StringBuilder

class PictureSearch : FunctionListener {
    private val map = HashMap<Long, Boolean>()

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val senderID = event.sender.id
        if (message == "/搜图" && !map.contains(senderID)) {
            map.put(senderID, true)
            event.reply(At(event.sender as Member) + "请发送图片")
        }

        if (map[senderID] != null && image != null) {
            map.remove(senderID)
            event.reply(At(event.sender as Member) + "查找中!")
            val results = PictureSearchUtil.search(image.queryUrl())
            if (results.isEmpty()) {
                event.reply("未查找到结果!")
                map.remove(senderID)
                return true
            }
            results.forEach {
                val builder = StringBuilder()
                it.extUrls.forEach {
                    builder.append(it).append("\n")
                }
                event.reply(event.uploadImage(NetWorkUtil.get(it.thumbnail)!!.first) as Message + PlainText("\n相似度: ${it.similarity} \n画师名: ${it.memberName} \n相关链接: \n${builder.toString().replace(Regex("\n$"), "")}"))
            }
        }

        return true
    }


}