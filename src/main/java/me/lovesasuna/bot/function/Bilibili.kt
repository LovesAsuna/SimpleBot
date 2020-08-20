package me.lovesasuna.bot.function

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.uploadAsImage
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class Bilibili : FunctionListener {
    private val pattern = Pattern.compile("BV(\\d|[a-z]|[A-Z]){10}")

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {

        var av: String?
        var bv: String?
        var reader: BufferedReader?
        var inputStream: InputStream?
        if (message.toLowerCase().contains("av")) {
            av = BasicUtil.extractInt(message).toString()
            inputStream = NetWorkUtil.get("https://api.bilibili.com/x/web-interface/view?aid=$av")?.second
        } else if (message.contains("BV")) {
            val matcher = pattern.matcher(message)
            bv = if (matcher.find()) {
                matcher.group()
            } else {
                return false
            }
            inputStream = NetWorkUtil.get("https://api.bilibili.com/x/web-interface/view?bvid=$bv")?.second
        } else {
            return false
        }
        if (inputStream == null) {
            return false
        }
        reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        val line = reader.readLine()
        if (!line.startsWith("{\"code\":0")) {
            return false
        }
        val mapper = ObjectMapper()
        val jsonNode = mapper.readTree(line)
        val dataObject = jsonNode["data"]
        val pic = dataObject["pic"].asText();
        val title = dataObject["title"].asText()
        val UP = dataObject["owner"]["name"].asText()
        val uplink = dataObject["owner"]["mid"].asText()
        val zone = dataObject["tname"].asText()
        val statObject = dataObject["stat"]
        val view = statObject["view"].asText()
        val Barrage = statObject["danmaku"].asText()
        val reply = statObject["reply"].asText()
        val fav = statObject["favorite"].asText()
        val coin = statObject["coin"].asText()
        val share = statObject["share"].asText()
        val like = statObject["like"].asText()
        val desc = dataObject["desc"].asText()
        val builder = StringBuilder("\n" + title)
        builder.append("\nUP: ")
                .append(UP)
                .append("(https://space.bilibili.com/")
                .append(uplink)
                .append(")\n分区: ")
                .append(zone)
                .append("\n播放量: ")
                .append(view)
                .append(" 弹幕: ")
                .append(Barrage)
                .append(" 评论: ")
                .append(reply)
                .append("\n收藏: ")
                .append(fav)
                .append(" 投币: ")
                .append(coin)
                .append(" 分享: ")
                .append(share)
                .append(" 点赞: ")
                .append(like)
                .append("\n")
                .append(desc)
        event.reply(NetWorkUtil.get(pic)!!.second.uploadAsImage(event.sender) + builder.toString())
        return true
    }

}