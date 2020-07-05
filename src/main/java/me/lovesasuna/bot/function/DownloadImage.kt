package me.lovesasuna.bot.function


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.lovesasuna.bot.util.interfaces.Listener
import me.lovesasuna.bot.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.queryUrl
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DownloadImage : Listener {
    companion object {
        var max = 0
        val path = "C:/Users/${System.getenv()["USERNAME"]}/Desktop/image"

        fun init() {
            Sort.sort("png")
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

        init {
            init()
        }
    }

    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        /*如果图片不为空*/
        val imageURL = image?.queryUrl()
        GlobalScope.async {
            if (imageURL != null) {
                val result = NetWorkUtil.get(imageURL) ?: return@async
                val size = result.second
                if (size < 650000) {
                    return@async
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
            }
        }
        return true
    }
}