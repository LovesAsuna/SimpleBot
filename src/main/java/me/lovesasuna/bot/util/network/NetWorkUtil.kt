package me.lovesasuna.bot.util.network

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:08
 */
object NetWorkUtil {
    @JvmStatic
    fun get(urlString: String?, vararg headers: Array<String>): Triple<Int, InputStream, Int>? {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            connect(conn, *headers)
            val responseCore = conn.responseCode
            val inputStream = if (responseCore == 200) conn.inputStream else conn.errorStream
            val length = conn.contentLength
            Triple(responseCore, inputStream, length)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun connect(conn: HttpURLConnection, vararg headers: Array<String>) {
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
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun inputStreamClone(inputStream: InputStream): ByteArrayOutputStream? {
        return try {
            var baos = ByteArrayOutputStream()
            var buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > -1) {
                baos.write(buffer, 0, len)
            }
            baos.flush()
            baos
        } catch (e: IOException) {
            e.printStackTrace();
            null
        }
    }

    @JvmStatic
    fun post(urlString: String?, body: ByteArray, vararg headers: Array<String>): Triple<Int, InputStream, Int>? {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.doOutput = true
            connect(conn, *headers)
            val outputStream = conn.outputStream
            val writer = DataOutputStream(outputStream)
            writer.write(body)
            writer.flush()
            val responseCore = conn.responseCode
            val inputStream = if (responseCore == 200) conn.inputStream else conn.errorStream
            val length = conn.contentLength
            Triple(responseCore, inputStream, length)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}