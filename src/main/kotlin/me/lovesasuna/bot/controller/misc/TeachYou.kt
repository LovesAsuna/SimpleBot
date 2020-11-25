package me.lovesasuna.bot.controller.misc

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.util.network.UrlUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import sun.net.util.URLUtil
import java.net.URLEncoder
import java.util.*

/**
 * @author LovesAsuna
 **/
class TeachYou : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val msg: String
        val url: String
        val sequence = message.split(" ")
        when {
            message.startsWith("/百度 ") -> {
                msg = "百度"
                url = "https://u.iheit.com/teachsearch/baidu/index.html?q=${getSuffix(message)}"
            }
            message.startsWith("/谷歌 ") -> {
                msg = "谷歌"
                url = "https://u.iheit.com/teachsearch/google/index.html?q=${getSuffix(message)}"
            }
            message.startsWith("/bing ") -> {
                msg = "必应"
                url = "https://u.iheit.com/teachsearch/bing/index.html?q=${getSuffix(message)}"
            }
            message.startsWith("/搜狗 ") -> {
                msg = "搜狗"
                url = "https://u.iheit.com/teachsearch/sougou/index.html?q=${getSuffix(message)}"
            }
            else -> return false
        }

        event.reply(
            """
            点击以下链接即可教您使用${msg}搜索“${sequence[1]}“
            ${UrlUtil.shortUrl(url)}
            """.trimIndent()
        )
        return true
    }

    private fun getSuffix(message : String) : String{
        val content = message.split(" ")[1]
        return URLEncoder.encode(Base64.getEncoder().encodeToString(content.toByteArray()), "UTF-8")
    }
}