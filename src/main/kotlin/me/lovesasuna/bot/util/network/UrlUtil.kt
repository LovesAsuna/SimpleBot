package me.lovesasuna.bot.util.network

import java.io.IOException
import java.net.URLEncoder


object UrlUtil {
    fun shortUrl(url: String): String {
        return try {
            val node =  OkHttpUtil.getJson("https://c34.cn/api/?key=NCq7UkhAxi83&url=" + URLEncoder.encode(
                url,
                "utf-8"
            ))
            if (node.get("error").asInt() == 0) {
                node.get("short").asText()
            } else node.get("msg").asText()
        } catch (e: IOException) {
            e.printStackTrace()
            "短链接异常！！"
        }
    }
}