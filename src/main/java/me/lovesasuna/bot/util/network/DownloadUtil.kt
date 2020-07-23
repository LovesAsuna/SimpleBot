package me.lovesasuna.bot.util.network

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

/**
 * @author LovesAsuna
 * @date 2020/2/17 16:45
 */
object DownloadUtil {
    /**
     * @param urlString 下砸地址
     * @param fileName 不包含路径的文件名
     * @param savePath 不包含文件名的路径
     * @param consumer 消费者函数，对下载时的字节长度进行处理
     * @param heads 请求头
     */
    fun download(urlString: String?, fileName: String, savePath: String, consumer: (Int) -> Unit = {}, vararg heads: Array<String>): Boolean {
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
            download(conn, fileName, savePath, consumer)
            true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * @param conn 已经设置好属性并已经连接的HttpURLConnection
     * @param fileName 不包含路径的文件名
     * @param savePath 不包含文件名的路径
     * @param consumer 消费者函数，对下载时的字节长度进行处理
     */
    @Throws(IOException::class)
    fun download(conn: HttpURLConnection, fileName: String, savePath: String, consumer: (Int) -> Unit = {}) {
        download(conn, File(savePath + File.separator + fileName), consumer)
    }

    /**
     * @param conn 已经设置好属性并已经连接的HttpURLConnection
     * @param file 文件绝对路径
     * @param consumer 消费者函数，对下载时的字节长度进行处理
     */
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

    /**
     * @param id 蓝奏云地址后的字符串
     * @param fileName 不包含路径的文件名
     * @param savePath 不包含文件名的路径
     * @param consumer 消费者函数，对下载时的字节长度进行处理
     */
    fun lanzousDownload(id: String, fileName: String, savePath: String, consumer: (Int) -> Unit = {}) {
        download(getlanzousUrl(id), fileName, savePath, consumer, arrayOf("Accept-Language", "zh-CN,zh;q=0.9"))
    }

    /**
     * @param id 蓝奏云地址后的字符串
     */
    fun getlanzousUrl(id: String): String {
        val lanzousUrl = "https://wwa.lanzous.com/$id"
        var reader = NetWorkUtil.get(lanzousUrl)!!.first.bufferedReader()
        for (i in 0 until 45) reader.readLine()
        val src = reader.readLine().also { reader.close() }
        val fn = src.split("\"")[5]
        val fnurl = "https://wwa.lanzous.com$fn"
        reader = NetWorkUtil.get(fnurl)!!.first.bufferedReader()
        val result = Regex("'\\w.*_c_c'").run {
            this.find(reader.lines().filter { it.contains(this) }.collect(Collectors.toList()).first())!!.value
        }
        val sign = result.split("'")[1]
        val data = "action=downprocess&sign=$sign&ves=1"
        reader = NetWorkUtil.post("https://wwa.lanzous.com/ajaxm.php", data.toByteArray(),
                arrayOf("Referer", fnurl),
                arrayOf("Cookie", "noads=1; pc_ad1=1"),
                arrayOf("Host", "wwa.lanzous.com")
        )!!.first.bufferedReader()
        val magic = reader.readLine().run {
            split("\"")[9]
        }
        return "https://vip.d0.baidupan.com/file/$magic"
    }
}