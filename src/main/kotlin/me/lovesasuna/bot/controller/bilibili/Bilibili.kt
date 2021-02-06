package me.lovesasuna.bot.controller.bilibili

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.util.regex.Pattern

object Bilibili : RawCommand(
    owner = Main,
    primaryName = "/converse",
    description = "B站视频解析",
    parentPermission = registerDefaultPermission()
) {
    private val pattern = Pattern.compile("BV(\\d|[a-z]|[A-Z]){10}")

    override suspend fun CommandSender.onCommand(args: MessageChain) {
        lateinit var av: String
        lateinit var bv: String

        val line = OkHttpUtil.getStr(
            when {
                args.contentToString().toLowerCase().contains("av") -> {
                    av = BasicUtil.extractInt(args.content).toString()
                    "https://api.bilibili.com/x/web-interface/view?aid=$av"
                }
                args.contentToString().contains("BV") -> {
                    val matcher = pattern.matcher(args.content)
                    bv = if (matcher.find()) {
                        matcher.group()
                    } else {
                        return
                    }
                    "https://api.bilibili.com/x/web-interface/view?bvid=$bv"
                }
                else -> {
                    IllegalArgumentException("不正确的视频ID").let { "" }
                }
            }
        )
        if (!line.startsWith("{\"code\":0")) {
            return
        }
        val mapper = ObjectMapper()
        val jsonNode = mapper.readTree(line)
        val dataObject = jsonNode["data"]
        val pic = dataObject["pic"].asText()
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
        sendMessage(OkHttpUtil.getIs(OkHttpUtil[pic]).uploadAsImage(getGroupOrNull()!!) + builder.toString())
    }
}