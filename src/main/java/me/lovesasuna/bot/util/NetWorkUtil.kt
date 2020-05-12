package me.lovesasuna.bot.util

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:08
 */
object NetWorkUtil {
    @JvmStatic
    fun fetch(urlString: String?, vararg headers: Array<String>): Pair<InputStream, Int>? {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                connectTimeout = 5 * 1000
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")
                for (header in headers) {
                    setRequestProperty(header[0], header[1])
                }
                connect()
            }
            val inputStream = conn.inputStream
            val length = conn.contentLength
            Pair(inputStream, length)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}