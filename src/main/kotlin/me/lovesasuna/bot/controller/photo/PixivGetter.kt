package me.lovesasuna.bot.controller.photo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.network.OkHttpUtil
import java.io.InputStream

class PixivGetter : FunctionListener {
    @ExperimentalCoroutinesApi
    override suspend fun execute(box: MessageBox): Boolean {
        when {
            box.text().startsWith("/pixiv work ") -> {
                val ID = BasicUtil.extractInt(box.text().split(" ")[2])
                box.reply("获取中,请稍后..")
                val reader = OkHttpUtil.getIs(
                    OkHttpUtil.post(
                        "https://api.pixiv.cat/v1/generate", mapOf(
                            "p" to "$ID"
                        )
                    )
                ).bufferedReader()
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
                            OkHttpUtil.getIs(OkHttpUtil["https://api.kuku.me/pixiv/picbyurl?url=${list.asText()}"])
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
                            OkHttpUtil.getIs(OkHttpUtil["https://api.kuku.me/pixiv/picbyurl?url=${list[it].asText()}"])
                        box.reply(box.uploadImage(originInputStream!!))
                    }
                    box.reply("获取完成!")
                }
            }
        }
        return true
    }


}