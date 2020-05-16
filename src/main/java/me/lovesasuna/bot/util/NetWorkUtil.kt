package me.lovesasuna.bot.util

import java.io.ByteArrayOutputStream
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
                try {
                    connect()
                } catch (e: IOException) {
                    return null
                }
            }
            val responseCore = conn.responseCode
            val inputStream = if (responseCore == 200) conn.inputStream else conn.errorStream
            val length = conn.contentLength
            Pair(inputStream, length)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun inputStreamClone(inputStream: InputStream): ByteArrayOutputStream? {
        try {
            var baos = ByteArrayOutputStream()
            var buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > -1) {
                baos.write(buffer, 0, len)
            }
            baos.flush()
            return baos
        } catch (e : IOException) {
            e.printStackTrace();
            return null
        }

    }
}