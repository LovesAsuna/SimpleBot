package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.io.InputStream

class PixivGetter : FunctionListener {
    @ExperimentalCoroutinesApi
    override suspend fun execute(box: MessageBox): Boolean {
        when {
            box.text().startsWith("/pixiv work ") -> {
                val ID = BasicUtil.extractInt(box.text().split(" ")[2])
                box.reply("获取中,请稍后..")
                val reader = NetWorkUtil.post(
                    "https://api.pixiv.cat/v1/generate", "p=$ID".toByteArray(),
                    arrayOf("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                )!!.second.bufferedReader()
                val root = BotData.objectMapper.readTree(reader.readLine())
                val list = root.get("original_url") ?: root.get("original_urls")
                if (list == null) {
                    box.reply("该作品不存在或已被删除!")
                    return false
                }
                val size = list.size()
                var originInputStream: InputStream?
                if (size == 0) {
                    if (BotData.debug) box.reply("尝试复制IO流")
                    Main.scheduler.withTimeOut(suspend {
                        originInputStream =
                            NetWorkUtil["https://api.kuku.me/pixiv/picbyurl?url=${list.asText()}"]!!.second
                        val uploadImage = box.uploadImage(originInputStream!!)
                        box.reply(uploadImage)
                        box.reply("获取完成!")
                    }, 60 * 1000) {
                        box.reply("图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者")
                    }
                } else {
                    box.reply("该作品共有${size}张图片")
                    repeat(size) {
                        originInputStream =
                            NetWorkUtil["https://api.kuku.me/pixiv/picbyurl?url=${list[it].asText()}"]!!.second
                        box.reply(box.uploadImage(originInputStream!!))
                    }
                    box.reply("获取完成!")
                }
            }
            box.text().contains("i.pximg.net") -> {
                box.reply(box.uploadImage(NetWorkUtil[box.text().replace("i.pximg.net", "/i.pixiv.cat")]!!.second))
            }
        }
        return true
    }


}