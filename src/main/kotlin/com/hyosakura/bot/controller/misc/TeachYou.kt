package com.hyosakura.bot.controller.misc

import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.util.network.UrlUtil
import java.net.URLEncoder
import java.util.*

/**
 * @author LovesAsuna
 **/
class TeachYou : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        val message = box.text()
        box.reply(
            when {
                message.startsWith("/百度 ") -> {
                    Baidu().searchUrl(message)
                }
                message.startsWith("/谷歌 ") -> {
                    Google().searchUrl(message)
                }
                message.startsWith("/bing ") -> {
                    Bing().searchUrl(message)
                }
                message.startsWith("/搜狗 ") -> {
                    Sogou().searchUrl(message)
                }
                else -> return false
            }
        )
        return true
    }
}

private interface SearchFactory {
    fun searchUrl(message: String): String
}

sealed class AbstractSearch(val engineID: String, val engineName: String) : SearchFactory {
    override fun searchUrl(message: String): String {
        val url = "https://u.iheit.com/teachsearch/${engineID}/index.html?q=${getSuffix(message)}"
        val searchContent = message.split(" ")[1]
        return """
            点击以下链接即可教您使用${engineName}搜索"$searchContent"
            ${UrlUtil.shortUrl(url)}
            """.trimIndent()
    }

    private fun getSuffix(message: String): String {
        val content = message.split(" ")[1]
        return URLEncoder.encode(Base64.getEncoder().encodeToString(content.toByteArray()), "UTF-8")
    }
}

private class Baidu : AbstractSearch("baidu", "百度")
private class Google : AbstractSearch("google", "谷歌")
private class Bing : AbstractSearch("bing", "必应")
private class Sogou : AbstractSearch("sogou", "搜狗")