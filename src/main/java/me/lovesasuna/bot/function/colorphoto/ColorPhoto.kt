package me.lovesasuna.bot.function.colorphoto

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.util.interfaces.FunctionListener
import me.lovesasuna.bot.util.interfaces.PhotoSource
import me.lovesasuna.bot.util.network.NetWorkUtil
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText

class ColorPhoto : FunctionListener {
    lateinit var photoSource: PhotoSource
    var random = true
    var pixiv = true
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val bannotice = { GlobalScope.async { event.reply("该图源已被禁用！") } }
        if (message.startsWith("/色图")) {
            when (message.split(" ")[1]) {
                "pixiv" -> {
                    if (pixiv) {
                        photoSource = Pixiv()
                        val data = photoSource.fetchData()
                        val url = data?.split("|")?.get(0)
                        val quota = data?.split("|")?.get(1)
                        event.reply(event.uploadImage(NetWorkUtil.get(url)!!.first) + PlainText("\n剩余次数: $quota"))
                    } else {
                        bannotice.invoke()
                    }

                }
                "random" -> {
                    if (random) {
                        photoSource = Random()
                        event.reply(event.uploadImage(NetWorkUtil.get(photoSource.fetchData())!!.first))
                    } else {
                        bannotice.invoke()
                    }
                }
                "switch" -> {
                    changeBanStatus(event, message)
                }
            }
        }
        return true
    }

    private fun changeBanStatus(event: MessageEvent, message: String) {
        if (event.sender.id == Config.data.admin) {
            GlobalScope.async {
                when (message.split(" ")[2]) {
                    "pixiv" -> {
                        event.reply("已${if (pixiv) "禁用" else "解禁"}pixiv图源")
                        pixiv = !pixiv
                    }
                    "random" -> {
                        event.reply("已${if (random) "禁用" else "解禁"}random图源")
                        random = !random
                    }
                }
            }

        }
    }

}