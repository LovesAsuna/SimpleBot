package me.lovesasuna.bot.function


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.pushError
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.file.FileUtil
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.lanzou.bean.User
import me.lovesasuna.lanzou.file.FileAdapter
import me.lovesasuna.lanzou.util.NetWorkUtil
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
                e.pushError()
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
                var result = NetWorkUtil.get(imageURL)

                val size = result!!.third
                if (size < 650000) {
                    return@launch
                }

                if (!File(path).exists()) {
                    Files.createDirectories(Paths.get(file.path))
                }
                val inputstream = result.second
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
                        val user = User(Config.data.lanzouCookie)
                        val file = BasicUtil.getLocation("Bot${File.separator}$time.zip")
                        val fileAdapter = FileAdapter(user, file)
                        fileAdapter.upload()
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