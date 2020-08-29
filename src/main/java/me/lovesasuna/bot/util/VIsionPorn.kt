package me.lovesasuna.bot.util

import me.lovesasuna.bot.data.pushError
import me.lovesasuna.lanzou.util.NetWorkUtil
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest

fun main() {
    //todo 腾讯API未知问题，有待解决
    val url = "https://s1.hdslb.com/bfs/static/jinkela/video/asserts/cm_2.png"
    val time = (System.currentTimeMillis() / 1000).toString()
    val builder = StringBuilder()

    val map = hashMapOf(
            "app_id" to "2149521182",
            "time_stamp" to time,
            "nonce_str" to "none",
            "image_url" to URLEncoder.encode(url, "UTF-8")
    )

    map.apply {
        keys.sortedWith(String.CASE_INSENSITIVE_ORDER).forEach {
            builder.append("$it=${this[it]}&")
        }
        builder.append("app_key=AMESxkS7y5oJBvS6")
    }

    var origin = builder.toString()
    println(origin)
    val sign = crypt(origin).toUpperCase()
    map["sign"] = sign
    println(sign)
    map.apply {
        this["image_url"] = url
        builder.setLength(0)
        keys.forEach {
            builder.append("$it=${this[it]}&")
        }
    }
    origin = builder.toString().replace(Regex("&$"), "")
    println(origin)
    val api = "https://api.ai.qq.com/fcgi-bin/vision/vision_porn"
    val reader = NetWorkUtil.post(api, origin.toByteArray(), arrayOf("Content-Type", "application/x-www-form-urlencoded"))!!.second.bufferedReader()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        println(line)
    }
}

fun crypt(str: String): String {
    return try {
        val md = MessageDigest.getInstance("MD5")
        md.update(str.toByteArray())
        BigInteger(1, md.digest()).toString(16)
    } catch (e: Exception) {
        e.pushError()
        throw Exception("MD5加密出现错误，$e")
    }
}