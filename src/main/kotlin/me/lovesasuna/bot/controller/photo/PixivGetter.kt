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
            box.message().startsWith("/pixiv work ") -> {
                val ID = BasicUtil.extractInt(box.message().split(" ")[2])
                var reader = NetWorkUtil["https://api.imjad.cn/pixiv/v1/?type=illust&id=$ID"]!!.second.bufferedReader()
                var root = BotData.objectMapper.readTree(reader.readLine())
                val status = root["status"].asText()
                if (BotData.debug) box.reply("R级检测响应: $status")
                var count = 1
                if (status == "failure") {
                    box.reply("查询图片信息失败，跳过R级检测...")
                } else {
                    val tags = root["response"][0]["tags"].toString()
                    count = root["response"][0]["page_count"].asInt()
                    if (BotData.debug) box.reply(tags)
                    if (tags.contains(Regex("R-[1-9]+"))) {
                        box.reply("图片含有R18内容,禁止显示！")
                        return false
                    }
                }

                box.reply("获取中,请稍后..")
                reader = NetWorkUtil.post(
                    "https://api.pixiv.cat/v1/generate", "p=$ID".toByteArray(),
                    arrayOf("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                )!!.second.bufferedReader()
                root = BotData.objectMapper.readTree(reader.readLine())
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
                    box.reply("该作品共有${count}张图片")
                    repeat(size) {
                        originInputStream =
                            NetWorkUtil["https://api.kuku.me/pixiv/picbyurl?url=${list[it].asText()}"]!!.second
                        box.reply(box.uploadImage(originInputStream!!))
                    }
                    box.reply("获取完成!")
                }
            }
            box.message().contains("i.pximg.net") -> {
                box.reply(box.uploadImage(NetWorkUtil[box.message().replace("i.pximg.net", "/i.pixiv.cat")]!!.second))
            }
        }
        return true
    }


}