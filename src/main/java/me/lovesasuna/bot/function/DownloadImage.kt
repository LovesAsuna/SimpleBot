package me.lovesasuna.bot.function


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.lovesasuna.bot.data.ConfigData
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.file.FileUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.network.DownloadUtil
import me.lovesasuna.bot.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.queryUrl
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

class DownloadImage : FunctionListener {
    companion object {
        var max = 0
        val file = BasicUtil.getLocation("Bot${File.separator}DownloadedImage")
        val path = file.path

        fun getIndex() {
            File(path).apply {
                if (!this.exists()) {
                    Files.createDirectories(this.toPath())
                }
            }
            try {
                max = 0
                Files.list(Paths.get(path)).use { pathStream ->
                    pathStream.forEach { p: Path ->
                        val i = p.toFile().name.split(".").toTypedArray()[0].toInt()
                        if (i > max) {
                            max = i
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun init() {
            getIndex()
            Sort.sort("png")
            getIndex()
        }

        init {
            init()
        }
    }

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        /*如果图片不为空*/
        val imageURL = image?.queryUrl()
        GlobalScope.launch {
            if (imageURL != null) {
                var result: Pair<InputStream, Int>? = null
                try {
                    result = NetWorkUtil.get(imageURL)
                } catch (e: SocketTimeoutException) {
                }
                val size = result!!.second
                if (size < 650000) {
                    return@launch
                }
                val file = File(path)
                if (!file.exists()) {
                    Files.createDirectories(Paths.get(file.path))
                }
                val inputstream = result.first
                val bytes = ByteArray(2048)
                var length: Int
                val fileOutputStream = FileOutputStream(file.path + File.separator + ++max + ".png")

                while (inputstream.read(bytes).also { length = it } != -1) {
                    fileOutputStream.write(bytes, 0, length)
                }
                fileOutputStream.close()
                if (max >= 250) {
                    GlobalScope.launch {
                        val time = SimpleDateFormat("MM月dd日HH时mm分").format(Date())
                        FileUtil.toZip(path, BasicUtil.getLocation("Bot${File.separator}$time.zip").path)
                        DownloadUtil.Lanzou.uploadFile(BasicUtil.getLocation("Bot${File.separator}$time.zip"),
                                Config.data.lanzouCookie,
                                0)
                        max = 0
                        Companion.file.listFiles().forEach {
                            it.delete()
                        }
                    }
                }
            }
        }
        return true
    }
}