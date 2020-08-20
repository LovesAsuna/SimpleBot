package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

class PixivCat : FunctionListener {
    @ExperimentalCoroutinesApi
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        when {
            message.startsWith("/pixiv work ") -> {
                val ID = BasicUtil.extractInt(message.split(" ")[2])
                val reader = NetWorkUtil.get("https://api.imjad.cn/pixiv/v1/?type=illust&id=$ID")!!.second.bufferedReader()
                val root = ObjectMapper().readTree(reader.readLine())
                val status = root["status"].asText()
                if (BotData.debug) event.reply("R级检测相应: $status")
                if (status == "failure") {
                    event.reply("查询图片信息失败，跳过R级检测...")
                } else {
                    val tags = root["response"][0]["tags"].toString()
                    if (BotData.debug) event.reply(tags)
                    if (tags.contains(Regex("R-[1-9]+"))) {
                        event.reply("图片含有R18内容,禁止显示！")
                        return false
                    }
                }

                val result = NetWorkUtil.get("https://pixiv.cat/$ID.jpg")
                val originInputStream = result!!.second
                val responseCode = result.first
                event.reply("获取中,请稍后..")
                if (responseCode != 404) {
                    if (BotData.debug) event.reply("尝试复制IO流")
                    Main.scheduler.withTimeOut(suspend {
                        if (BotData.debug) event.reply("计时1分钟")
                        val byteArrayOutputStream = NetWorkUtil.inputStreamClone(originInputStream)
                        if (BotData.debug) event.reply("IO流复制完成，开始上传图片!")
                        val uploadImage = event.uploadImage(ByteArrayInputStream(byteArrayOutputStream?.toByteArray()))
                        event.reply(uploadImage)
                        event.reply("获取完成!")
                        byteArrayOutputStream
                    }, 60 * 1000) {
                        event.reply("图片获取失败,大概率是服务器宽带问题或图片过大，请捐赠支持作者")
                    }
                } else {
                    val byteArrayOutputStream = NetWorkUtil.inputStreamClone(originInputStream)
                    val string = ByteArrayInputStream(byteArrayOutputStream?.toByteArray()).bufferedReader().lineSequence().joinToString()
                    val matcher = Pattern.compile("這個作品ID中有 \\d+ 張圖片").matcher(string)
                    if (matcher.find()) {
                        val num = BasicUtil.extractInt(matcher.group())
                        event.reply("该作品共有${num}张图片")
                        repeat(num) {
                            val inputStream = NetWorkUtil.get("https://pixiv.cat/$ID-${it + 1}.jpg")!!.second
                            event.reply(event.uploadImage(inputStream))
                        }
                    } else {
                        event.reply("该作品不存在或已被删除!")
                    }
                }
            }
            message.contains("i.pximg.net") -> {
                event.reply(event.uploadImage(NetWorkUtil.get(message.replace("i.pximg.net", "/i.pixiv.cat"))!!.second))
            }
        }
        return true
    }


}