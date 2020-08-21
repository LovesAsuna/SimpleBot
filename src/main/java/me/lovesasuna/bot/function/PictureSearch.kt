package me.lovesasuna.bot.function

import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.pictureSearchUtil.Ascii2d
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.network.NetWorkUtil
import me.lovesasuna.bot.util.pictureSearchUtil.Saucenao
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.*
import java.lang.StringBuilder

class PictureSearch : FunctionListener {
    private val map = HashMap<Long, Int>()

    @ExperimentalCoroutinesApi
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val senderID = event.sender.id
        if (message.startsWith("/搜图 ") && !map.contains(senderID)) {
            map[senderID] = BasicUtil.extractInt(message.split(" ")[1], 1)
            event.reply(At(event.sender as Member) + "请发送图片")
        }

        if (map[senderID] != null && image != null) {
            val source = when (map[senderID]) {
                1 -> {
                    event.reply(At(event.sender as Member) + "Saucenao查找中!")
                    Saucenao
                }
                2 -> {
                    event.reply(At(event.sender as Member) + "Ascii2d查找中!")
                    Ascii2d
                }
                else -> Saucenao
            }
            val imgUrl = image.queryUrl()
            if (BotData.debug) event.reply("图片URL: $imgUrl")
            val results = source.search(imgUrl)
            if (results.isEmpty()) {
                event.reply("未查找到结果!")
                map.remove(senderID)
                return true
            }
            event.reply("搜索完成!")
            map.remove(senderID)
            if (BotData.debug) event.reply(results.toString())
            results.forEach { result ->
                val builder = StringBuilder()
                result.extUrls.forEach {
                    builder.append(it).append("\n")
                }
                Main.scheduler.withTimeOut(suspend {
                    val uploadImage = event.uploadImage(NetWorkUtil.get(result.thumbnail)!!.second) as Message
                    event.reply(uploadImage + PlainText("\n相似度: ${result.similarity} \n画师名: ${result.memberName} \n相关链接: \n${builder.toString().replace(Regex("\n$"), "")}"))
                    uploadImage
                }, 5 * 1000) {
                    event.reply("缩略图上传超时")
                    event.reply(PlainText("空图像(上传失败)\n相似度: ${result.similarity} \n画师名: ${result.memberName} \n相关链接: \n${builder.toString().replace(Regex("\n$"), "")}"))
                }

            }
        }
        return true
    }


}