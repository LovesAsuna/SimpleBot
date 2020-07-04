package me.lovesasuna.bot.util

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author LovesAsuna
 * @date 2020/2/17 16:45
 */
object DownloadUtil {
    fun download(urlString: String?, fileName: String, savePath: String, vararg heads: Array<String>): Boolean {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                for (head in heads) {
                    setRequestProperty(head[0], head[1])
                }
                connect()
            }
            download(conn, fileName, savePath)
            true
        } catch (e: IOException) {
            false
        }
    }

    @Throws(IOException::class)
    fun download(conn: HttpURLConnection, fileName: String, savePath: String) {
        download(conn, File(savePath + File.separator + fileName)) {}
    }

    @Throws(IOException::class)
    fun download(conn: HttpURLConnection, file: File, consumer: (Int) -> Unit) {
        val inputStream = conn.inputStream
        var length: Int
        val bytes = ByteArray(2048)
        val fileOutputStream = FileOutputStream(file)
        while (inputStream.read(bytes).also { length = it } != -1) {
            consumer.invoke(length)
            fileOutputStream.write(bytes, 0, length)
        }
        fileOutputStream.close()
        conn.disconnect()
    }
}