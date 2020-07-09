package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.withTimeout
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

class PixivCat : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        when {
            message.startsWith("/pixiv work ") -> {
                val ID = BasicUtil.ExtraceInt(message.split(" ")[2])
                val reader = NetWorkUtil.get("https://api.imjad.cn/pixiv/v1/?type=illust&id=$ID")!!.first.bufferedReader()
                val root = ObjectMapper().readTree(reader.readLine())
                val status = root["status"].asText()
                if (status == "failure") {
                    event.reply("查询图片信息失败，跳过R级检测...")
                } else {
                    val tags = root["response"][0]["tags"].toString()
                    if (tags.contains(Regex("R-[1-9]+"))) {
                        event.reply("图片含有R18内容,禁止显示！")
                        return false
                    }
                }

                val orignInputStream = NetWorkUtil.get("https://pixiv.cat/$ID.jpg")!!.first
                event.reply("获取中,请稍后..")
                val byteArrayOutputStream = NetWorkUtil.inputStreamClone(orignInputStream)
                try {
                    withTimeout(7500) {
                        event.reply(event.uploadImage(ByteArrayInputStream(byteArrayOutputStream?.toByteArray())))
                        event.reply("获取完成!")
                    }
                } catch (e: Exception) {
                    val string = ByteArrayInputStream(byteArrayOutputStream?.toByteArray()).bufferedReader().lineSequence().joinToString()
                    val matcher = Pattern.compile("這個作品ID中有 \\d 張圖片").matcher(string)
                    if (matcher.find()) {
                        val num = BasicUtil.ExtraceInt(matcher.group())
                        event.reply("该作品共有${num}张图片")
                        repeat(num) {
                            val inputStream = NetWorkUtil.get("https://pixiv.cat/$ID-${it + 1}.jpg")!!.first
                            event.reply(event.uploadImage(inputStream))
                        }
                    } else {
                        event.reply("该作品不存在或已被删除!")
                    }
                }

            }
            message.contains("i.pximg.net") -> {
                event.reply(event.uploadImage(NetWorkUtil.get(message.replace("i.pximg.net", "/i.pixiv.cat"))!!.first))
            }
        }
        return true
    }


}