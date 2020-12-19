package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.pictureSearch.Ascii2d
import me.lovesasuna.bot.util.pictureSearch.Saucenao
import me.lovesasuna.lanzou.util.NetWorkUtil
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.queryUrl

class PictureSearch : FunctionListener {
    private val map = HashMap<Long, Int>()

    @ExperimentalCoroutinesApi
    override suspend fun execute(box: MessageBox): Boolean {
        val senderID = box.sender.id
        val at = At(box.sender as Member)
        if (box.text().startsWith("/搜图 ") && !map.contains(senderID)) {
            map[senderID] = BasicUtil.extractInt(box.text().split(" ")[1], 1)
            box.reply(at + "请发送图片")
            return true
        }

        if (map[senderID] != null && box.image() != null) {
            val source = when (map[senderID]) {
                1 -> {
                    map.remove(senderID)
                    box.reply(at + "Saucenao查找中!")
                    Saucenao
                }
                2 -> {
                    map.remove(senderID)
                    box.reply(at + "Ascii2d查找中!")
                    Ascii2d
                }
                else -> Saucenao
            }
            val imgUrl = box.image()!!.queryUrl()
            if (BotData.debug) box.reply("图片URL: $imgUrl")
            val results = source.search(imgUrl)
            if (results.isEmpty()) {
                box.reply("未查找到结果!")
                return true
            }
            box.reply("搜索完成!")
            if (BotData.debug) box.reply(results.toString())
            results.forEach { result ->
                val builder = StringBuilder()
                result.extUrls.forEach {
                    builder.append(it).append("\n")
                }
                Main.scheduler.withTimeOut(suspend {
                    val uploadImage = box.event.uploadImage(NetWorkUtil[result.thumbnail]!!.second) as Message
                    box.reply(
                        uploadImage + PlainText(
                            "\n相似度: ${result.similarity} \n画师名: ${result.memberName} \n相关链接: \n${
                                builder.toString().replace(Regex("\n$"), "")
                            }"
                        )
                    )
                    uploadImage
                }, 7500) {
                    box.reply("缩略图上传超时")
                    box.reply(
                        PlainText(
                            "空图像(上传失败)\n相似度: ${result.similarity} \n画师名: ${result.memberName} \n相关链接: \n${
                                builder.toString().replace(Regex("\n$"), "")
                            }"
                        )
                    )
                }

            }
        }
        return true
    }
}