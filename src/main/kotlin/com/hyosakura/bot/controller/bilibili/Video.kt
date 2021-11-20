package com.hyosakura.bot.controller.bilibili

import com.fasterxml.jackson.databind.ObjectMapper
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.util.BasicUtil
import com.hyosakura.bot.util.network.OkHttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.util.regex.Pattern

class Video : FunctionListener {
    private val avPattern = Pattern.compile("[aA][vV]\\d*")
    private val bvPattern = Pattern.compile("BV(\\d|[a-z]|[A-Z]){10}")

    override suspend fun execute(box: MessageBox): Boolean {

        lateinit var av: String
        lateinit var bv: String
        val args = box.text()
        val url = when {
            args.lowercase().contains("av") -> {
                val matcher = avPattern.matcher(args)
                av = if (matcher.find()) {
                    matcher.group()
                } else {
                    return false
                }
                av = BasicUtil.extractInt(av).toString()
                "https://api.bilibili.com/x/web-interface/view?aid=$av"
            }
            args.contains("BV") -> {
                val matcher = bvPattern.matcher(args)
                bv = if (matcher.find()) {
                    matcher.group()
                } else {
                    return false
                }
                "https://api.bilibili.com/x/web-interface/view?bvid=$bv"
            }
            else -> {
                return false
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        val line = withContext(Dispatchers.IO) {
            OkHttpUtil.getStr(url)
        }
        if (!line.startsWith("{\"code\":0")) {
            return false
        }
        val mapper = ObjectMapper()

        @Suppress("BlockingMethodInNonBlockingContext")
        val jsonNode = withContext(Dispatchers.IO) {
            mapper.readTree(line)
        }
        val dataObject = jsonNode["data"]
        val pic = dataObject["pic"].asText()
        val title = dataObject["title"].asText()
        val up = dataObject["owner"]["name"].asText()
        val uplink = dataObject["owner"]["mid"].asText()
        val zone = dataObject["tname"].asText()
        val statObject = dataObject["stat"]
        val view = statObject["view"].asText()
        val barrage = statObject["danmaku"].asText()
        val reply = statObject["reply"].asText()
        val fav = statObject["favorite"].asText()
        val coin = statObject["coin"].asText()
        val share = statObject["share"].asText()
        val like = statObject["like"].asText()
        val desc = dataObject["desc"].asText()
        val builder = StringBuilder("\n" + title)
        builder.append("\nUP: ")
            .append(up)
            .append("(https://space.bilibili.com/")
            .append(uplink)
            .append(")\n分区: ")
            .append(zone)
            .append("\n播放量: ")
            .append(view)
            .append(" 弹幕: ")
            .append(barrage)
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
        box.reply(OkHttpUtil.getIs(OkHttpUtil[pic]).uploadAsImage(box.group!!) + builder.toString())
        return true
    }

}